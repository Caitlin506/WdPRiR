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

public class Mandelbrot_parts {
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

    public BufferedImage simulation (int pxW, int pxH, double minW, double maxW, double minH, double maxH, int maxIter, int part) {
        BufferedImage bufferedImage = new BufferedImage(pxW, pxH, BufferedImage.TYPE_INT_RGB);

        int parts_x = pxW/part;
        int parts_y = pxH/part;

        ExecutorService ex = Executors.newFixedThreadPool(4);
        for (int i = 0; i<parts_x; i++){
            for (int j = 0; j<parts_y; j++) {
                int[] my_part = new int[] {i,j};
                //System.out.println(my_part[1]);
                ex.execute(() -> Mandelbrot_parts.generatePart(pxW, pxH, minW, maxW, minH, maxH, maxIter, my_part, parts_x, parts_y, bufferedImage));
            }
        }
        ex.shutdown();
        try {
            ex.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return bufferedImage;
    }
    public static void generatePart(int pxW, int pxH, double minW, double maxW, double minH, double maxH, int maxIter, int[] part, int nparts_x, int nparts_y, BufferedImage bf) {
        double dx = (maxW - minW) / (pxW - 1);
        double dy = (maxH - minH) / (pxH - 1);

        int startX = pxW/nparts_x * part[0];
        int stopX = pxW/nparts_x + startX;
        int startY = pxH/nparts_y * part[1];
        int stopY = pxH/nparts_y + startY;

        for (int x=startX; x<stopX; x++) {
            for (int y = startY; y < stopY; y++) {
                double myX = minW + x * dx;
                double myY = maxH - y * dy;
                int color = calculateColor(myX, myY, maxIter);
                bf.setRGB(x, y, color);
            }
        }
    }
    public void averageTime (int size, int repeats, int[] blocks) {
        double[] times = new double [blocks.length];
        for (int i=0; i<blocks.length; i++) {
            System.out.println(blocks[i]);
            long start = System.nanoTime();
            for (int j=0; j<repeats; ++j) {
                this.simulation(size,size,-2.1,0.6,-1.2,1.2,200, blocks[i]);
            }
            long end = System.nanoTime();
            times[i] = (end-start)/1e9/repeats;
        }

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("blocks_times.txt"));
            for (int i=0; i<blocks.length; i++) {
                writer.write(blocks[i]+"\t"+times[i]+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int[] sizes = new int[] {32,64,128,256,512,1024,2048,4096,8192};
        int repeats = 10;
        int[] blocks = new int[] {4,8,16,32,64,128};
        int size = 4096;
        Mandelbrot_parts m = new Mandelbrot_parts();
        //BufferedImage myImage = m.simulation(size,size, -2.1,0.6,-1.2,1.2,200, 128);
        m.averageTime(size, repeats, blocks);
//        File outputfile = new File("image"+size+".jpg");
//        try {
//            ImageIO.write(myImage, "jpg", outputfile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //System.out.println("Procesory: " + Runtime.getRuntime().availableProcessors());
    }

}
