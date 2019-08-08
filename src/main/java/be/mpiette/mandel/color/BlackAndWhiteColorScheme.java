package be.mpiette.mandel.color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlackAndWhiteColorScheme implements ColorScheme {

	Logger LOGGER = LoggerFactory.getLogger(BlackAndWhiteColorScheme.class);

	private final int maximum;

	public BlackAndWhiteColorScheme(int maximum) {
		this.maximum = maximum;
	}

	@Override public Color map(int number) {
		assert number <= maximum;
		assert number >= 0;

		if (number == maximum) {
			return Color.WHITE;
		}

		int red = Double.valueOf((255.0 * (double) number / (double) maximum)).intValue();

		return new Color(red, red, red);
	}

	@Override public List<Color> generate() {
		return IntStream.range(0, maximum).mapToObj(i -> map(i)).collect(Collectors.toList());
	}
}
