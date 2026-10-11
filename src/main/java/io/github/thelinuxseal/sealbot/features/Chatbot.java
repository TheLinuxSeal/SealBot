package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.SealBot;
import io.github.thelinuxseal.sealbot.Permission;
import io.github.thelinuxseal.sealbot.ruletypes.AdvancedFunctionRule;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Official java-llama.cpp ecosystem imports
import de.kherud.llama.LlamaModel;
import de.kherud.llama.LlamaOutput;
import de.kherud.llama.ModelParameters;
import de.kherud.llama.InferenceParameters;

public class Chatbot {

    public static final AdvancedFunctionRule CHATBOT = new AdvancedFunctionRule(
            Permission.NORMAL,
            Pattern.compile("\\bseal\\s+answer\\s+(.+)$", Pattern.CASE_INSENSITIVE),
            Chatbot::answer
    );

    // Highly optimized Q4_K_M quantization profile recommended for native C++ bindings
    public static final String AI_PATH = "qwen2.5-0.5b-instruct-q4_k_m.gguf";
    public static final String DOWNLOAD_URL =
            "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf";

    // Static runtime model handler reference
    private static LlamaModel modelInstance;
    private static boolean isDownloading = false;

    public static List<String> answer(Matcher m, SealBot.MessageData data) {
        String question = m.group(1).trim();

        // Run asynchronously so the Minecraft render/engine ticks never hitch
        CompletableFuture.runAsync(() -> {
            try {
                File modConfigDir = new File(Minecraft.getInstance().gameDirectory, "config/sealbot");
                if (!modConfigDir.exists()) modConfigDir.mkdirs();

                File modelFile = new File(modConfigDir, AI_PATH);

                // 1. Download missing model weights safely
                if (!modelFile.exists()) {
                    if (isDownloading) {
                        SealBot.queueMessage(List.of("AI Status: Model download is currently in progress..."), data);
                        return;
                    }
                    isDownloading = true;
                    SealBot.queueMessage(List.of("AI Status: Downloading Llama 3.2 (~1.2 GB) into mod configs folder..."), data);

                    File tmp = new File(modConfigDir, AI_PATH + ".part");
                    try (var in = URI.create(DOWNLOAD_URL).toURL().openStream()) {
                        Files.copy(in, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    try (var raf = new java.io.RandomAccessFile(tmp, "r")) {
                        byte[] magic = new byte[4];
                        raf.readFully(magic);
                        if (!new String(magic, java.nio.charset.StandardCharsets.US_ASCII).equals("GGUF")) {
                            tmp.delete();
                            throw new java.io.IOException("Downloaded file is not a GGUF model");
                        }
                    }
                    Files.move(tmp.toPath(), modelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    isDownloading = false;
                    SealBot.queueMessage(List.of("AI Status: Download complete! Mounting native C++ inference engine..."), data);
                }

                // 2. Initialize the native model weights mapping sequence
                if (modelInstance == null) {
                    ModelParameters modelParams = new ModelParameters()
                            .setModel(modelFile.getAbsolutePath())
                            .setCtxSize(512) // Strict path allocation
                            //.setContextSize(512) // Low profile restriction limits active heap usage
                            .setThreads(Math.max(1, Runtime.getRuntime().availableProcessors() / 2));

                    // OPTIONAL GPU TRACKING: If players have a CUDA or Metal setup,
                    // you can uncomment this line to dynamically offload layer calculations:
                    // .setGpuLayers(32)

                    modelInstance = new LlamaModel(modelParams);
                }

                // 3. Define the prompt sequence context boundaries
                String fullPrompt = """
<|im_start|>system
You are a Minecraft bot. Answer with a single short phrase or word. Never use full sentences.<|im_end|>
<|im_start|>user
%s<|im_end|>
<|im_start|>assistant
""".formatted(question);

                // 4. Configure real-time execution parameters
                InferenceParameters inferParams = new InferenceParameters(fullPrompt)
                        .setTemperature(0.4f)
                        .setNPredict(48)
                        .setStopStrings("<|im_end|>", "<|eot_id|>");

                StringBuilder responseBuilder = new StringBuilder();

                // 5. Evaluate the tensor graph via streaming token cycles natively
                for (LlamaOutput output : modelInstance.generate(inferParams)) {
                    responseBuilder.append(output.toString());
                }

                String cleanAnswer = responseBuilder.toString().trim();
                if (cleanAnswer.isEmpty()) {
                    cleanAnswer = "...";
                }

                // 6. Push decoded text payload directly back to player chat queue
                SealBot.queueMessage(List.of(cleanAnswer), data);

            } catch (Exception e) {
                e.printStackTrace();
                isDownloading = false;
                SealBot.queueMessage(List.of("AI error: Native engine failed computation loop."), data);
            }
        });

        return Collections.emptyList();
    }
}
