package net.machinemuse.general.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.machinemuse.general.geometry.Colour;
import net.machinemuse.general.geometry.FlyFromPointToPoint2D;
import net.machinemuse.general.geometry.MuseRenderer;
import net.machinemuse.general.geometry.Point2D;
import net.machinemuse.general.gui.clickable.Clickable;
import net.machinemuse.general.gui.frame.IGuiFrame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * I got fed up with Minecraft's gui system so I wrote my own (to some extent.
 * Still based on GuiScreen). This class contains a variety of helper functions
 * to draw geometry and various other prettifications. Note that MuseGui is
 * heavily geometry-based as opposed to texture-based.
 * 
 * @author MachineMuse
 * 
 */
public class MuseGui extends GuiScreen {
	protected static RenderItem renderItem;
	protected final boolean usePretty = true;
	protected static final Tessellator tesselator = Tessellator.instance;
	protected Point2D ul, br;
	protected long creationTime;
	protected int xSize;
	protected int ySize;
	
	protected List<IGuiFrame> frames;
	
	public MuseGui() {
		super();
		frames = new ArrayList();
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override public void initGui() {
		super.initGui();
		this.controlList.clear();
		Keyboard.enableRepeatEvents(true);
		creationTime = System.currentTimeMillis();
		
		int xpadding = (width - getxSize()) / 2;
		int ypadding = (height - ySize) / 2;
		ul = new FlyFromPointToPoint2D(absX(0), absY(0), absX(-1), absY(-1),
				150);
		br = new FlyFromPointToPoint2D(absX(0), absY(0), absX(1), absY(1), 150);
	}
	
	/**
	 * Draws the gradient-rectangle background you see in the TinkerTable gui.
	 */
	public void drawRectangularBackground() {
		MuseRenderer.drawFrameRect(
				ul.x(), ul.y(),
				br.x(), br.y(),
				new Colour(0.1F, 0.9F, 0.1F, 0.8F),
				new Colour(0.0F, 0.2F, 0.0F, 0.8F),
				this.zLevel, 8f);
	}
	/**
	 * Adds a frame to this gui's draw list.
	 * 
	 * @param frame
	 */
	public void addFrame(IGuiFrame frame) {
		frames.add(frame);
	}
	
	/**
	 * Draws all clickables in a list!
	 */
	public void drawClickables(List<? extends Clickable> list) {
		if (list == null) {
			return;
		}
		Iterator<? extends Clickable> iter = list.iterator();
		Clickable clickie;
		while (iter.hasNext())
		{
			clickie = iter.next();
			clickie.draw();
		}
	}
	
	/**
	 * Draws the background layer for the GUI.
	 */
	public void drawBackground() {
		this.drawDefaultBackground(); // Shading on the world view
		this.drawRectangularBackground(); // The window rectangle
	}
	
	/**
	 * Called every frame, draws the screen!
	 */
	@Override public void drawScreen(int x, int y, float z) {
		super.drawScreen(x, y, z);
		drawBackground();
		for (IGuiFrame frame : frames) {
			frame.draw();
		}
		drawToolTip();
	}
	
	/**
	 * Returns the first ID in the list that is hit by a click
	 * 
	 * @return
	 */
	public int hitboxClickables(int x, int y,
			List<? extends Clickable> list) {
		if (list == null) {
			return -1;
		}
		Clickable clickie;
		for (int i = 0; i < list.size(); i++)
		{
			clickie = list.get(i);
			if (clickie.hitBox(x, y)) {
				// MuseLogger.logDebug("Hit!");
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Whether or not this gui pauses the game in single player.
	 */
	@Override public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	/**
	 * Returns absolute screen coordinates (int 0 to width) from a relative
	 * coordinate (float -1.0F to +1.0F)
	 * 
	 * @param relx
	 *            Relative X coordinate
	 * @return Absolute X coordinate
	 */
	public int absX(float relx) {
		int absx = (int) ((relx + 1) * getxSize() / 2);
		int xpadding = (width - getxSize()) / 2;
		return absx + xpadding;
	}
	
	/**
	 * Returns relative coordinate (float -1.0F to +1.0F) from absolute
	 * coordinates (int 0 to width)
	 * 
	 */
	public int relX(float absx) {
		int padding = (width - getxSize()) / 2;
		int relx = (int) ((absx - padding) * 2 / getxSize() - 1);
		return relx;
	}
	
	/**
	 * Returns absolute screen coordinates (int 0 to width) from a relative
	 * coordinate (float -1.0F to +1.0F)
	 * 
	 * @param relx
	 *            Relative Y coordinate
	 * @return Absolute Y coordinate
	 */
	public int absY(float rely) {
		int absy = (int) ((rely + 1) * ySize / 2);
		int ypadding = (height - ySize) / 2;
		return absy + ypadding;
	}
	
	/**
	 * Returns relative coordinate (float -1.0F to +1.0F) from absolute
	 * coordinates (int 0 to width)
	 * 
	 */
	public int relY(float absy) {
		int padding = (height - ySize) / 2;
		int rely = (int) ((absy - padding) * 2 / ySize - 1);
		return rely;
	}
	
	/**
	 * @return the xSize
	 */
	public int getxSize() {
		return xSize;
	}
	
	/**
	 * @param xSize
	 *            the xSize to set
	 */
	public void setxSize(int xSize) {
		this.xSize = xSize;
	}
	
	/**
	 * @return the ySize
	 */
	public int getySize() {
		return ySize;
	}
	
	/**
	 * @param ySize
	 *            the ySize to set
	 */
	public void setySize(int ySize) {
		this.ySize = ySize;
	}
	
	/**
	 * Called when the mouse is clicked.
	 */
	@Override protected void mouseClicked(int x, int y, int button)
	{
		if (button == 0) // Left Mouse Button
		{
			for (IGuiFrame frame : frames) {
				frame.onMouseDown(x, y);
			}
		}
	}
	/**
	 * @param x
	 * @param y
	 */
	protected void drawToolTip() {
		int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int y = this.height - Mouse.getEventY() * this.height
				/ this.mc.displayHeight - 1;
		List<String> tooltip = getToolTip(x, y);
		if (tooltip != null) {
			int strwidth = 0;
			for (String s : tooltip) {
				int currstrwidth = MuseRenderer.getFontRenderer()
						.getStringWidth(s);
				if (currstrwidth > strwidth) {
					strwidth = currstrwidth;
				}
			}
			int top, bottom, left, right;
			if (y > this.height / 2) {
				top = y - 10 * tooltip.size() - 8;
				bottom = y;
				left = x;
				right = x + 8 + strwidth;
			} else {
				top = y;
				bottom = y + 10 * tooltip.size() + 8;
				
				left = x + 4;
				right = x + 12 + strwidth;
			}
			
			MuseRenderer.drawFrameRect(
					left, top,
					right, bottom,
					new Colour(0.2F, 0.6F, 0.9F, 0.7F),
					new Colour(0.1F, 0.3F, 0.4F, 0.7F),
					0.0F, 4f);
			for (int i = 0; i < tooltip.size(); i++) {
				MuseRenderer.drawString(tooltip.get(i),
						left + 4,
						bottom - 10 * (tooltip.size() - i) - 4);
			}
		}
	}
	
	/**
	 * @return
	 */
	public List<String> getToolTip(int x, int y) {
		List<String> hitTip = null;
		for (IGuiFrame frame : frames) {
			hitTip = frame.getToolTip(x, y);
			if (hitTip != null) {
				return hitTip;
			}
		}
		return null;
	}
	
	/**
	 * 
	 */
	public void refresh() {}
}