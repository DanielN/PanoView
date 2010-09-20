package panoview;

import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import panoview.lens.FisheyeLens;
import panoview.lens.RectilinearLens;

public class PanoToolBar extends Panel implements ItemListener {

	private static final long serialVersionUID = 1L;

	private final Choice lensChoice;
	private final Checkbox smoothCheckbox;
	private final PanoView panoView;

	public PanoToolBar(PanoView panoView) {
		this.panoView = panoView;
		lensChoice = new Choice();
		lensChoice.add("Rectilinear");
		lensChoice.add("Fisheye");
		lensChoice.addItemListener(this);
		add(lensChoice);
		smoothCheckbox = new Checkbox("Smooth");
		smoothCheckbox.addItemListener(this);
		add(smoothCheckbox);
	}

	@Override
	public void itemStateChanged(ItemEvent ev) {
		if (ev.getSource() == lensChoice) {
			if (ev.getItem().equals("Rectilinear")) {
				panoView.setLens(new RectilinearLens());
			} else if (ev.getItem().equals("Fisheye")) {
				panoView.setLens(new FisheyeLens());
			}
		} else if (ev.getSource() == smoothCheckbox) {
			panoView.getTexture().setSmooth(smoothCheckbox.getState());
			panoView.repaint();
		}
	}
}
