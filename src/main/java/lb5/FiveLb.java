package lb5;

import mpi.MPI;
import mpi.Prequest;

import java.util.Locale;
import java.util.Random;

public class FiveLb {
    public static void run(String[] args) {
        Locale.setDefault(Locale.US);
        int n = 12, m = 12;
        double[][] a = new double[m + 2][n + 2];
        double[][] b = new double[m][n];
        MPI.Init(args);
        Prequest[] req = new Prequest[4];
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int left, right;
        int countIteration;
        if (rank == 0) {
            generateRandomMatrix(a);
            for (int i = 1; i < m + 1; i++) {
                System.arraycopy(a[i], 1, b[i - 1], 0, n + 1 - 1);
            }
            printMatrix(a, 0);
//            printMatrix(b, 0);
            System.out.println();
        }

        if (rank == 0) {
            left = MPI.PROC_NULL;
        } else {
            left = rank - 1;
        }

        if (rank == size - 1) {
            right = MPI.PROC_NULL;
        } else {
            right = rank + 1;
        }


        if (rank != 0)
            req[0] = MPI.COMM_WORLD.Send_init(b[0], 0, n, MPI.DOUBLE, left, 5);
        if (rank != size - 1)
            req[1] = MPI.COMM_WORLD.Send_init(b[m - 1], 0, n, MPI.DOUBLE, right, 5);
        if (rank != 0)
            req[2] = MPI.COMM_WORLD.Recv_init(a[0], 1, n, MPI.DOUBLE, left, 5);
        if (rank != size - 1)
            req[3] = MPI.COMM_WORLD.Recv_init(a[m + 1], 1, n, MPI.DOUBLE, right, 5);

        for (countIteration = 0; countIteration < 100; countIteration++) {
            for (int i = 1; i <= n; i++) {
                b[0][i - 1] = 0.25 * (a[0][i] + a[2][i] + a[1][i + 1] + a[1][i - 1]);
                b[m - 1][i - 1] = 0.25 * (a[m][i - 1] + a[m][i + 1] + a[m - 1][i] + a[m + 1][i]);
            }
            Prequest.Startall(req);

            for (int j = 2; j <= m - 1; j++) {
                for (int i = 1; i <= n; i++) {
                    b[j - 1][i - 1] = 0.25 * (a[j][i - 1] + a[j][i + 1] + a[i][j - 1] + a[i][j + 1]);
                }
            }

            for (int j = 1; j <= m; j++) {
                System.arraycopy(b[j - 1], 0, a[j], 1, n);
            }

            Prequest.Waitall(req);
        }

        if (rank == 0) {
            printMatrix(a, countIteration);
        }
        MPI.Finalize();
    }

    static void printMatrix(double[][] array, int countIteration) {
        StringBuffer sb = new StringBuffer();
        sb.append("iteration ").append(countIteration).append("\n");
        for (double[] doubles : array) {
            for (int i = 0; i < doubles.length; i++) {
                sb.append(String.format("%8.3f", doubles[i]));
            }
            sb.append("\n");
        }
        sb.append("----------------------------------------------------------------------------------------------------------------");
        System.out.println(sb.toString());
    }

    static void generateRandomMatrix(double[][] array) {
        Random r = new Random();
        for (int j = 0; j < array.length; j++) {
            for (int i = 0; i < array[j].length; i++) {
                array[j][i] = r.nextInt(11);
            }

        }
    }
}
