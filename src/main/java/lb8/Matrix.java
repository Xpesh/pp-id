package lb8;
import mpi.MPI;

import java.util.Arrays;

public class Matrix {
    public static void run(String[] args) {

        int n = 4;
        int m = 2;
        int k = 5;

        double[][] a = new double[n][m];
        double[][] b = new double[m][k];
        double[][] c = new double[n][k];
        double[][] cr = new double[n][k];

        int rank, size;

        MPI.Init(args);

        rank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();

        if (rank == 0) {

            a[0][0] = 1;
            a[0][1] = 2;
            a[1][0] = 3;
            a[1][1] = 4;
            a[2][0] = 5;
            a[2][1] = 6;
            a[3][0] = 7;
            a[3][1] = 8;

            b[0][0] = 9;
            b[0][1] = 10;
            b[0][2] = 11;
            b[0][3] = 12;
            b[0][4] = 13;
            b[1][0] = 14;
            b[1][1] = 15;
            b[1][2] = 16;
            b[1][3] = 17;
            b[1][4] = 18;
        }

        int[] each = new int[size];
        int[] offsets = new int[size];

        Arrays.fill(each, n / size);
        int count = n % size;

        for (int i = 0; i < count; i++) {
            each[i]++;
        }

        for (int i = 1; i < offsets.length; i++) {
            offsets[i] = offsets[i-1] + each[i-1];

        }

        double[][] ai = new double[each[rank]][m];

        MPI.COMM_WORLD.Scatterv(a, 0, each, offsets, MPI.OBJECT, ai, 0, each[rank], MPI.OBJECT, 0);
        System.arraycopy(ai, 0, a, offsets[rank], ai.length);
        MPI.COMM_WORLD.Bcast(b, 0, b.length, MPI.OBJECT, 0);

        //int start = rank * n / size;
        //int end = (rank + 1) * n / size;

        //int start = rank == 0 ? 0 : rank * each[rank-1];
        //int end = start + each[rank];

        int start = offsets[rank];
        int end = start + each[rank];

        for (int i = start; i < end; i++) {
            for (int j = 0; j < k; j++) {
                for (int l = 0; l < m; l++) {
                    c[i][j] += a[i][l] * b[l][j];
                }
            }
        }

        if (rank == 2) {
            printMatrix(c);
        }


        MPI.COMM_WORLD.Barrier();

        //Arrays.fill(offsets, 0);

        MPI.COMM_WORLD.Gatherv(c, offsets[rank], each[rank], MPI.OBJECT, cr, 0, each, offsets, MPI.OBJECT, 0);


        MPI.COMM_WORLD.Barrier();

        if (rank == 0) {

            System.out.println("first matrix:");
            printMatrix(a);
            System.out.println("second matrix:");
            printMatrix(b);
            System.out.println("result matrix:");
            printMatrix(cr);
        }
        MPI.Finalize();
    }

    public static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%6.0f", matrix[i][j]);
            }
            System.out.println();
        }
    }
}
