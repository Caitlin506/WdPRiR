import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Mandelbrot_pula {
    public static int calculateColor (double x, double y, int maxIter) {
        ComplexNumber number = new ComplexNumber(x, y);
        ComplexNumber z = number;
        int i;
        for (i=0; i<maxIter; i++){
            z = z.multiply(z).add(number);
            if (z.absolute()>2.0){
                Color c = new Color(149+106*i/maxIter,53*i/maxIter,83+42*i/maxIter);
                return c.getRGB();
            }
        }
        return 0x953553;
    }

    public BufferedImage simulation (int pxW, int pxH, double minW, double maxW, double minH, double maxH, int maxIter, ExecutorService ex) {
        BufferedImage bufferedImage = new BufferedImage(pxW, pxH, BufferedImage.TYPE_INT_RGB);
        int part1[] = new int[] {0,0};
        int part2[] = new int[] {0,1};
        int part3[] = new int[] {1,0};
        int part4[] = new int[] {1,1};

        // Pula tworzona za każdym razem
//        ExecutorService ex = Executors.newFixedThreadPool(4);
        ex.execute(() -> Mandelbrot_pula.generatePart(pxW, pxH, minW, maxW, minH, maxH, maxIter, part1, bufferedImage));
        ex.execute(() -> Mandelbrot_pula.generatePart(pxW, pxH, minW, maxW, minH, maxH, maxIter, part2, bufferedImage));
        ex.execute(() -> Mandelbrot_pula.generatePart(pxW, pxH, minW, maxW, minH, maxH, maxIter, part3, bufferedImage));
        ex.execute(() -> Mandelbrot_pula.generatePart(pxW, pxH, minW, maxW, minH, maxH, maxIter, part4, bufferedImage));
//        ex.shutdown();
//        try {
//            ex.awaitTermination(1, TimeUnit.DAYS);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return bufferedImage;
    }
    public static void generatePart(int pxW, int pxH, double minW, double maxW, double minH, double maxH, int maxIter, int[] part, BufferedImage bf) {
        double dx = (maxW - minW) / (pxW - 1);
        double dy = (maxH - minH) / (pxH - 1);

        int startX = pxW/2 * part[0];
        int stopX = pxW/2 + startX;
        int startY = pxH/2 * part[1];
        int stopY = pxH/2 + startY;

        for (int x=startX; x<stopX; x++) {
            for (int y = startY; y < stopY; y++) {
                double myX = minW + x * dx;
                double myY = maxH - y * dy;
                int color = calculateColor(myX, myY, maxIter);
                bf.setRGB(x, y, color);
            }
        }
    }
    public void averageTime (int[] N, int[] sizes) {
        ExecutorService ex = Executors.newFixedThreadPool(4);
        double [] times = new double [sizes.length];
        for (int i=0; i<sizes.length; i++) {
            System.out.println(sizes[i]);
            long start = System.nanoTime();
            for (int j=0; j<N[i]; ++j) {
                this.simulation(sizes[i],sizes[i],-2.1,0.6,-1.2,1.2,200, ex);
            }
            long end = System.nanoTime();
            times[i] = (end-start)/1e9/N[i];
        }
        ex.shutdown();
        try {
            ex.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("pool_times.txt"));
            for (int i=0; i<sizes.length; i++) {
                writer.write(sizes[i]*sizes[i]+"\t"+times[i]+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int[] sizes = new int[] {32,64,128,256,512,1024,2048,4096,8192};
        int[] repeats = new int[] {500,500,500,300,200,100,50,20,10}; // różne liczby powtórek monieważ największy obrazek generuje się 80s
        int size = 4096;
        Mandelbrot_pula m = new Mandelbrot_pula();
//        BufferedImage myImage = m.simulation(size,size, -2.1,0.6,-1.2,1.2,200);
//        File outputfile = new File("image"+size+".jpg");
//        try {
//            ImageIO.write(myImage, "jpg", outputfile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        System.out.println("Procesory: " + Runtime.getRuntime().availableProcessors());
        m.averageTime(repeats,sizes);
    }

}
