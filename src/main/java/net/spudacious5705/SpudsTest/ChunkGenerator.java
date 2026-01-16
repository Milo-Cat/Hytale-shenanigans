package net.spudacious5705.SpudsTest;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.lang.ref.WeakReference;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ChunkGenerator extends Thread {

    private AtomicBoolean running = new AtomicBoolean(false);

    private final WeakReference<World> world;

    private final Semaphore semaphore;

    private ExecutorService pool;

    private final int radius;

    private Consumer<String> messageSender;


    public ChunkGenerator(int radius, int parallelChunks, @NonNullDecl World world, CommandContext ctx){
        this.semaphore = new Semaphore(parallelChunks);
        this.radius = radius;
        this.world = new WeakReference<>(world);
        this.pool = Executors.newFixedThreadPool(parallelChunks);
        this.x = -radius;
        this.z = -radius;
        this.running.set(true);
        this.targetCount = radius*radius*4;
        this.messageSender = (s) -> ctx.sendMessage(Message.raw(s));
    }

    private int x;
    private int z;
    private int chunkCount = 0;
    private final int targetCount;
    private float percentage = 0.0f;

    @Override
    public void run() {
        while(running.get()){
            try {
                semaphore.acquire();
                var w = world.get();
                if(w != null) {
                    pool.submit(() -> generate(w, x, z));
                    advanceCords();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void advanceCords(){
        x++;
        chunkCount++;
        if (chunkCount % 100 == 0) {
            messageSender.accept("Generated chunks: " + chunkCount);
            int percent = (int) ((chunkCount * 100.0) / targetCount);
            messageSender.accept(percent + "% done!");
        }
        if(x>radius){
            z++;
            x = -radius;
            if(z>radius){
                running.set(false);
                messageSender.accept("PRE-GEN COMPLETE!");
            }
        }
    }

    private void generate(@NonNullDecl World world, int x, int z){
        long index = ChunkUtil.indexChunk(x,z);

        ChunkStore chunkStore = world.getChunkStore();
        try {

            chunkStore.getChunkReferenceAsync(index, 8).get(16, TimeUnit.SECONDS);

        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        semaphore.release();
    }
}
