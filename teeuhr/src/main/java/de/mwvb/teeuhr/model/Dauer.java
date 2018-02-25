package de.mwvb.teeuhr.model;

public class Dauer {
	private int minuten;
	private int sekunden;
	private boolean leer;
	
	public Dauer(String text) {
		try {
			int o = text.indexOf(",");
			if (o >= 0) {
				setMinuten(Integer.parseInt(text.substring(0, o)));
				setSekunden(Integer.parseInt(text.substring(o + 1)));
			} else {
				setMinuten(Integer.parseInt(text));
			}
			leer = false;
		} catch (NumberFormatException e) {
			leer = true;
		}
	}
	
	public int getMinuten() {
		return minuten;
	}

	public void setMinuten(int minuten) {
		this.minuten = minuten;
	}

	public int getSekunden() {
		return sekunden;
	}

	public void setSekunden(int sekunden) {
		this.sekunden = sekunden;
	}
	
	@Override
	public String toString() {
		return leer ? "-" : (minuten + "min " + sekunden + "sec");
	}

	public boolean isLeer() {
		return leer;
	}

	public void setLeer(boolean leer) {
		this.leer = leer;
	}
	
	public int getMillis() {
		return (minuten * 60 + sekunden) * 1000;
	}
}
