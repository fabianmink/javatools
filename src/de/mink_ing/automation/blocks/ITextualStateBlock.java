package de.mink_ing.automation.blocks;

public interface ITextualStateBlock extends IBlock {
	public String getStateAsString();
	public boolean isStateChanged();
}
