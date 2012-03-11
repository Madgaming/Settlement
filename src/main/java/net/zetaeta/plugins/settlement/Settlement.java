package net.zetaeta.plugins.settlement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Settlement implements Externalizable {

	@ToBeSaved
	private int bonusPlots;
	@ToBeSaved
	private String name;
	@ToBeSaved
	private String description;
	@ToBeSaved
	private 

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub

	}

}
