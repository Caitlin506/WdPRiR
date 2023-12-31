import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Mandelbrot {
    public static int calculateColor (double x, double y, int maxIter) {
        ComplexNumber number = new ComplexNumber(x, y);
        ComplexNumber z = number;
        int i;
        for (i=0; i<maxIter; i++){
            z = z.multiply(z).add(number);
            if (z.absolute()>2.0){
                Color c = new Color(149+106*i/maxIter,53*i/maxIter,83+42*i/maxIter);
                return c.getRGB();
            } // 255 255   0 żółty
            //// 149  53  83 fioletowy
        }
        return 0x953553; //hexadecymalny fioletowy
    }

    public BufferedImage simulation (int pxW, int pxH, double minW, double maxW, double minH, double maxH, int maxIter) {
        BufferedImage bufferedImage = new BufferedImage(pxW, pxH, BufferedImage.TYPE_INT_RGB);
        int part1[] = new int[] {0,0};
        int part2[] = new int[] {0,1};
        int part3[] = new int[] {1,0};
        int part4[] = new int[] {1,1};

        // Tradycyjne tworzenie wątków
        Thread t1 = new Thread(() -> Mandelbrot.generatePart(pxW, pxH, minW, maxW, minH, maxH, maxIter, part1, bufferedImage));
        Thread t2 = new Thread(() -> Mandelbrot.generatePart(pxW, pxH, minW, maxW, minH, maxH, maxIter, part2, bufferedImage));
        Thread t3 = new Thread(() -> Mandelbrot.generatePart(pxW, pxH, minW, maxW, minH, maxH, maxIter, part3, bufferedImage));
        Thread t4 = new Thread(() -> Mandelbrot.generatePart(pxW, pxH, minW, maxW, minH, maxH, maxIter, part4, bufferedImage));
        Thread[] threads = new Thread[] {t1,t2,t3,t4};
        for (int i=0; i<4; i++) {
            threads[i].start();
        }
        try {
            threads[0].join();
            threads[1].join();
            threads[2].join();
            threads[3].join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        double [] times = new double [sizes.length];
        for (int i=0; i<sizes.length; i++) {
            System.out.println(sizes[i]);
            long start = System.nanoTime();
            for (int j=0; j<N[i]; ++j) {
                this.simulation(sizes[i],sizes[i],-2.1,0.6,-1.2,1.2,200);
            }
            long end = System.nanoTime();
            times[i] = (end-start)/1e9/N[i];
        }

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("threads_times.txt"));
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
        int[] repeats = new int[] {500,500,500,300,200,100,50,20,10}; // różne liczby powtórek
        int size = 4096;
        Mandelbrot m = new Mandelbrot();
//        BufferedImage myImage = m.simulation(size,size, -2.1,0.6,-1.2,1.2,200);
//        File outputfile = new File("image"+size+".jpg");
//        try {
//            ImageIO.write(myImage, "jpg", outputfile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        m.averageTime(repeats,sizes);
    }

}
