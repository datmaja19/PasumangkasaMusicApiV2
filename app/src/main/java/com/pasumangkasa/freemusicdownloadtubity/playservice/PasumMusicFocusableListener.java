package com.pasumangkasa.freemusicdownloadtubity.playservice;

public interface PasumMusicFocusableListener {
	public void onGainedAudioFocus();
	public void onLostAudioFocus(boolean canDuck);
}
