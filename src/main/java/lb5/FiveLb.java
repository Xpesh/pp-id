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

        if (rank == 0) {
            generateRandomMatrix(a);
            for (int i = 1; i < m + 1; i++) {
                System.arraycopy(a[i], 1, b[i - 1], 0, n);
            }

            printMatrix(a, 0);
            System.out.println();
        }

        MPI.COMM_WORLD.Bcast(a, 0, n, MPI.OBJECT, 0);

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
            req[2] = MPI.COMM_WORLD.Recv_init(a[m], 1, n, MPI.DOUBLE, left, 5);
        if (rank != size - 1)
            req[3] = MPI.COMM_WORLD.Recv_init(a[1], 1, n, MPI.DOUBLE, right, 5);

        for (int c = 0; c < 100; c++) {

            for (int i = 1; i <= n; i++) {
                b[0][i - 1] = 0.25 * (a[0][i] + a[2][i] + a[1][i + 1] + a[1][i - 1]);
                b[m - 1][i - 1] = 0.25 * (a[m][i - 1] + a[m][i + 1] + a[m - 1][i] + a[m + 1][i]);
            }

            for (int j = 1; j <= m; j++) {
                for (int i = 1; i <= n; i++) {
                    b[j - 1][i - 1] = 0.25 * (a[j][i - 1] + a[j][i + 1] + a[j-1][i] + a[j+1][i]);
                }
            }

            for (int j = 1; j <= m; j++) {
                System.arraycopy(b[j - 1], 0, a[j], 1, n);
                if(rank==2) {
                    for (int k = j - 1; k < n; k++) {
                        System.out.print(a[j][k] + " ");
                    }
                }
            }
            if(rank==2) {
                printMatrix(a, c);
            }
//            if (rank == 0)
//                printMatrix(a, c);
        }

//        if (rank == 0) {
//            printMatrix(a, 100);
//        }
        MPI.Finalize();
    }

    static void printMatrix(double[][] array, int c) {
        StringBuffer sb = new StringBuffer();
        sb.append("iterarion ").append(c).append("\n");
        for (int j = 0; j < array.length; j++) {
            for (int i = 0; i < array[j].length; i++) {
                sb.append(String.format("%8.3f", array[j][i]));
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
