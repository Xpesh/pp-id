package lb7;
import mpi.*;

public class Line {
    public static void run(String[] args) {

        final int dimsNum = 1;
        int rank, size;
        Cartcomm cart;
        int[] a, b, c;
        int[] dims = new int[dimsNum];
        boolean[] periods = {false};
        int[] newCoords = new int[dimsNum];
        boolean reorders = false;

        MPI.Init(args);
        rank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();

        a = new int[]{rank};
        b = new int[]{-1};
        c = new int[]{-1};

        Cartcomm.Dims_create(size, dims);
        cart = MPI.COMM_WORLD.Create_cart(dims, periods, reorders);
        ShiftParms shift = cart.Shift(0, 1);

        if (rank == 0) {
            cart.Send(a, 0, 1, MPI.INT, shift.rank_dest, 12);
        } else if (rank == size - 1) {
            cart.Recv(b, 0, 1, MPI.INT, shift.rank_source, 12);
        } else {
            cart.Sendrecv(a, 0, 1, MPI.INT, shift.rank_dest, 12, b, 0, 1, MPI.INT, shift.rank_source, 12);
        }

        cart.Barrier();

        System.out.printf("shift right -> rank %d b = %d\n", rank, b[0]);

        cart.Barrier();

        shift = cart.Shift(0, -1);

        if (rank == 0) {
            cart.Recv(c, 0, 1, MPI.INT, shift.rank_source, 12);
        } else if (rank == size - 1) {
            cart.Send(b, 0, 1, MPI.INT, shift.rank_dest, 12);
        } else {
            cart.Sendrecv(b, 0, 1, MPI.INT, shift.rank_dest, 12, c, 0, 1, MPI.INT, shift.rank_source, 12);
        }

        cart.Barrier();

        System.out.printf("shift left  <- rank %d b = %d\n", rank, c[0]);
        cart.Free();
        MPI.Finalize();


    }
}
