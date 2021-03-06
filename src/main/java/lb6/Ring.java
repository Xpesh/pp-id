package lb6;

import mpi.*;

public class Ring {
    public static void run(String[] args) {
        final int dimsNum = 1;
        int rank, size;
        Cartcomm cart;
        int[] a, b;
        int[] dims = new int[dimsNum];
        boolean[] periods = {true};
        boolean reorders = false;

        MPI.Init(args);
        rank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();

        a = new int[]{rank};
        b = new int[]{-1};

        Cartcomm.Dims_create(size, dims);
        cart = MPI.COMM_WORLD.Create_cart(dims, periods, reorders);
        ShiftParms shift = cart.Shift(0, 1);

        if (rank == 0) {
            cart.Send(a, 0, 1, MPI.INT, shift.rank_source, 12);
            cart.Recv(b, 0, 1, MPI.INT, shift.rank_dest, 12);
            System.out.printf("rank = %d, a = %d, b = %d\n", rank, a[0], b[0]);
        } else {
            cart.Recv(b, 0, 1, MPI.INT, shift.rank_dest, 12);
            System.out.printf("rank = %d, a = %d, b = %d\n", rank, a[0], b[0]);
            cart.Send(a, 0, 1, MPI.INT, shift.rank_source, 12);
        }
        cart.Free();
        MPI.Finalize();
    }
}
