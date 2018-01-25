package eu.pmc.mpe;

import java.io.IOException;

import de.intarsys.tools.randomaccess.LERandomAccessByteArray;

public class MovieParam {
	
	private int address;
	private int width;
	private int height;
	private int fps;
	private short bitrate;
	private int flg1;
	private int aspectRatioId;
	
	boolean validMediaParam = true;
	private boolean modified = false;
	
	public MovieParam(int address, LERandomAccessByteArray fw) throws IOException {
		fw.seek(address);
		this.address = address;
		this.width = fw.readInt();
		this.height = fw.readInt();
		this.fps = fw.readInt();
		fw.seekBy(1);
		this.bitrate = fw.readShort();
		fw.seekBy(1);
		this.flg1 = fw.readInt();
		this.aspectRatioId = fw.readInt();
		if(width <= 10 || height <= 10 || fps <= 0 || bitrate <= 100 || (width / height)>= 10 || (height / width) >= 10 ) {
			validMediaParam = false;
		}
	}
	
	public MovieParam(int address, int width, int height, int fps, short bitrate, int flg1, int aspectRatioId) {
		this.address = address;
		this.width = width;
		this.height = height;
		this.fps = fps;
		this.bitrate = bitrate;
		this.flg1 = flg1;
		this.aspectRatioId = aspectRatioId;
	}

	@Override
	public String toString() {
		return "MediaParam [address=" + String.format("%08x", address) + ", width=" + width + ", height=" + height + ", fps=" + fps
				+ ", bitrate=" + bitrate + ", aspectRatioId=" + aspectRatioId + "]";
	}
	
	public boolean isValid() {
		return validMediaParam;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		modified = true;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		modified = true;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
		modified = true;
	}

	public int getBitrate() {
		return bitrate;
	}

	public void setBitrate(short bitrate) {
		this.bitrate = bitrate;
		modified = true;
	}

	public int getAddress() {
		return address;
	}
	
	public boolean isModified() {
		return modified;
	}

	@Deprecated
	public void setFlg1s(int flg1) {
		this.flg1 = flg1;
		modified = true;
	}
	
	@Deprecated
	public int getFlg1s() {
		return flg1;
	}
	
	public void setAspectRatioId(int id) {
		this.aspectRatioId = id;
		modified = true;
	}
	
	public int getAspectRatioId() {
		return this.aspectRatioId;
	}
	
	public byte[] toBytes() throws IOException {
		byte[] payload = new byte[24];
		LERandomAccessByteArray ra = new LERandomAccessByteArray(payload);
		ra.writeInt(getWidth());
		ra.writeInt(getHeight());
		ra.writeInt(getFps());
		ra.seekBy(1);
		ra.writeShort(getBitrate());
		ra.seekBy(1);
		ra.writeInt(flg1);
		ra.writeInt(aspectRatioId);
		ra.close();
		return payload;
	}
}
