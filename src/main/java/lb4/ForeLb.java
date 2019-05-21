package lb4;

import mpi.MPI;

public class ForeLb {
    public static void run(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int[] array = new int[size];
        long start = 0;

        if (rank == 0) {
            start = System.currentTimeMillis();
        }

        if (rank == 0) {
            System.out.print("Начальный ");
            printArray(array);
            MPI.COMM_WORLD.Send(array, 0, size, MPI.INT, rank + 1, 0);
            MPI.COMM_WORLD.Recv(array, 0, size, MPI.INT, size - 1, 0);
            array[rank]++;
            System.out.println("Я ПРОЦЕСС НОМЕР " + rank + ", ПОЛУЧЕНО СООБЩЕНИЕ ОТ ПРОЦЕССА " + (size - 1));
        } else {
            MPI.COMM_WORLD.Recv(array, 0, size, MPI.INT, rank - 1, 0);
            array[rank]++;
            printArray(array);
            System.out.println("Я ПРОЦЕСС НОМЕР " + rank + ", ПОЛУЧЕНО СООБЩЕНИЕ ОТ ПРОЦЕССА " + (rank - 1));
            int next = (rank + 1) % size;
            MPI.COMM_WORLD.Send(array, 0, size, MPI.INT, next, 0);
        }
        if (rank == 0) {
            System.out.println("ВРЕМЯ ВЫПОЛНЕНИЯ " + (System.currentTimeMillis() - start) + " МИЛЛИСЕКУНД");
            printArray(array);
        }
        MPI.Finalize();
    }

    private static void printArray(int[] ints){
        System.out.print("Массив = ");
        for(int i : ints){
            System.out.print(i + " ");
        }
        System.out.println();
    }
}
