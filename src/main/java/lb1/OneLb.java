package lb1;

import mpi.MPI;

public class OneLb {
    public static void run(String[] args) {
        int rank, size;
        double startwtime, endwtime;
        String name;

        MPI.Init(args);
        startwtime = MPI.Wtime();
        rank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();
        name = MPI.Get_processor_name();
//        load();
        endwtime = MPI.Wtime();
        System.out.println(String.format("Hello world from process %d of %d at %s as %f second",rank, size, name, endwtime - startwtime));
        MPI.Finalize();
    }

    private static void load(){
        for(long i=0;i<200000000L;i++){
            Math.sqrt(System.currentTimeMillis());
        }
    }
}
