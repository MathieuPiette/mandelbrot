package be.mpiette.mandel.parallel;

import java.io.IOException;

public interface ParallelCalculator {

	void process() throws IOException;

	int getTotalNumberOfOperations();
}
