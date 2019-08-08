package be.mpiette.mandel.color;

import java.awt.*;
import java.util.List;

public interface ColorScheme {

	Color map(int number);

	List<Color> generate();

}
