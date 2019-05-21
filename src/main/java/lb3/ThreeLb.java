package lb3;

import mpi.MPI;
import mpi.MPIException;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class ThreeLb {

    public static void run(String args[]){
        int[] intervals = new int[1];
        double[] mypi = new double[1];
        double[] pi = new double[1];
        long n;
        double h, x;
        double sum;
        try {
            MPI.Init(args);
            int me = MPI.COMM_WORLD.Rank();
            int size = MPI.COMM_WORLD.Size();
            if (me == 0) {
                Scanner scanner = new Scanner(new FileInputStream(new File("textIn.txt")));
                intervals[0] = scanner.nextInt();
            }
            MPI.COMM_WORLD.Bcast(intervals, 0, 1, MPI.INT, 0);
            n = intervals[0];
            if(n==0){
                MPI.Finalize();
                return;
            }
            h = 1.0 / (double) intervals[0];
            sum = 0.0;
            double start = System.currentTimeMillis();
            for (int i = me + 1; i <= n; i += size) {
                x = h * ((double) i - 0.5);
                sum += (4.0 / (1.0 + x * x));
            }
            mypi[0] = h * sum;
            double end = System.currentTimeMillis();
            MPI.COMM_WORLD.Reduce(mypi, 0, pi, 0, 1, MPI.DOUBLE, MPI.SUM, 0);
            if (me == 0) {
                System.out.format("Time: %f seconds\n", ((end - start)/ 1000L));
                System.out.println("Pi = " + pi[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MPI.Finalize();
        }
    }
}
