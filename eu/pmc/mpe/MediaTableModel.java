package eu.pmc.mpe;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class MediaTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private List<MovieParam> data;

	public MediaTableModel(List<MovieParam> l) {
		this.data = l;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return 6;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Address";
		case 1:
			return "Width";
		case 2:
			return "Height";
		case 3:
			return "FPS";
		case 4:
			return "Bitrate/2";
		case 5:
			return "Aspect ratio";
		default:
			return "?";
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		MovieParam mp = data.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return String.format("%08x", mp.getAddress());
		case 1:
			return mp.getWidth();
		case 2:
			return mp.getHeight();
		case 3:
			return mp.getFps();
		case 4:
			return mp.getBitrate();
		case 5:
			return mp.getAspectRatioId();
		default:
			return "Error";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return Integer.class;
		case 2:
			return Integer.class;
		case 3:
			return Integer.class;
		case 4:
			return Short.class;
		case 5:
			return Integer.class;
		default:
			return Object.class;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		MovieParam mp = data.get(rowIndex);
		if (aValue instanceof Integer && (columnIndex != 4)) {
			switch (columnIndex) {
			case 1:
				mp.setWidth((Integer) aValue);
				break;
			case 2:
				mp.setHeight((Integer) aValue);
				break;
			case 3:
				mp.setFps((Integer) aValue);
				break;
			case 5:
				mp.setAspectRatioId((Integer) aValue);
			}
		} else if (aValue instanceof Short && columnIndex == 4) {
			mp.setBitrate((Short) aValue);
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column > 0 ? true : false;
	}

}
