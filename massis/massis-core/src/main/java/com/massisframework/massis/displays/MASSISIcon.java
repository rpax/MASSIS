package com.massisframework.massis.displays;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.Icon;

/**
 * Icon representing MASSIS Icon
 *
 * @author rpax
 *
 */
public class MASSISIcon implements Icon {

	private float origAlpha = 1.0f;

	/**
	 * Paints the transcoded SVG image on the specified graphics context. You
	 * can install a custom transformation on the graphics context to scale the
	 * image.
	 *
	 * @param g
	 *            Graphics context.
	 */
	public void paint(Graphics2D g)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		origAlpha = 1.0f;
		Composite origComposite = g.getComposite();
		if (origComposite instanceof AlphaComposite)
		{
			AlphaComposite origAlphaComposite = (AlphaComposite) origComposite;
			if (origAlphaComposite.getRule() == AlphaComposite.SRC_OVER)
			{
				origAlpha = origAlphaComposite.getAlpha();
			}
		}

		// _0
		AffineTransform trans_0 = g.getTransform();
		paintRootGraphicsNode_0(g);
		g.setTransform(trans_0);

	}

	private void paintShapeNode_0_0_0(Graphics2D g)
	{
		GeneralPath shape0 = new GeneralPath();
		shape0.moveTo(248.0, 655.0);
		shape0.curveTo(255.69, 655.15, 255.48, 657.84, 259.58, 665.0);
		shape0.curveTo(259.58, 665.0, 273.0, 690.0, 273.0, 690.0);
		shape0.curveTo(273.0, 690.0, 275.0, 690.0, 275.0, 690.0);
		shape0.curveTo(275.0, 690.0, 288.58, 665.0, 288.58, 665.0);
		shape0.curveTo(288.58, 665.0, 295.38, 655.57, 295.38, 655.57);
		shape0.curveTo(295.38, 655.57, 314.0, 655.0, 314.0, 655.0);
		shape0.curveTo(314.0, 655.0, 314.0, 706.0, 314.0, 706.0);
		shape0.curveTo(314.0, 706.0, 301.0, 706.0, 301.0, 706.0);
		shape0.curveTo(301.0, 706.0, 300.0, 672.0, 300.0, 672.0);
		shape0.curveTo(300.0, 672.0, 287.7, 695.0, 287.7, 695.0);
		shape0.curveTo(286.22, 697.68, 283.31, 703.49, 280.79, 704.98);
		shape0.curveTo(278.64, 706.24, 274.5, 706.04, 272.0, 706.0);
		shape0.curveTo(264.17, 705.87, 264.21, 702.41, 260.58, 696.0);
		shape0.curveTo(260.58, 696.0, 247.0, 672.0, 247.0, 672.0);
		shape0.curveTo(247.0, 672.0, 247.0, 706.0, 247.0, 706.0);
		shape0.curveTo(247.0, 706.0, 234.0, 706.0, 234.0, 706.0);
		shape0.curveTo(234.0, 706.0, 234.0, 655.0, 234.0, 655.0);
		shape0.curveTo(234.0, 655.0, 248.0, 655.0, 248.0, 655.0);
		shape0.closePath();
		shape0.moveTo(335.75, 671.0);
		shape0.curveTo(337.39, 667.75, 341.75, 657.7, 344.39, 656.02);
		shape0.curveTo(346.73, 654.53, 354.01, 654.99, 357.0, 655.0);
		shape0.curveTo(367.09, 655.02, 364.34, 655.44, 371.86, 669.0);
		shape0.curveTo(371.86, 669.0, 391.0, 706.0, 391.0, 706.0);
		shape0.curveTo(391.0, 706.0, 375.39, 704.98, 375.39, 704.98);
		shape0.curveTo(375.39, 704.98, 366.9, 694.6, 366.9, 694.6);
		shape0.curveTo(366.9, 694.6, 348.0, 694.0, 348.0, 694.0);
		shape0.curveTo(348.0, 694.0, 339.63, 695.6, 339.63, 695.6);
		shape0.curveTo(339.63, 695.6, 333.37, 705.01, 333.37, 705.01);
		shape0.curveTo(333.37, 705.01, 318.0, 706.0, 318.0, 706.0);
		shape0.curveTo(318.0, 706.0, 335.75, 671.0, 335.75, 671.0);
		shape0.closePath();
		shape0.moveTo(401.83, 691.14);
		shape0.curveTo(407.16, 692.62, 404.98, 695.94, 418.0, 696.0);
		shape0.curveTo(421.91, 696.02, 429.46, 696.03, 432.92, 694.44);
		shape0.curveTo(437.06, 692.55, 437.0, 688.52, 432.92, 686.74);
		shape0.curveTo(430.78, 685.82, 427.34, 686.08, 425.0, 685.91);
		shape0.curveTo(425.0, 685.91, 413.0, 685.0, 413.0, 685.0);
		shape0.curveTo(402.25, 684.98, 391.19, 685.88, 391.0, 672.0);
		shape0.curveTo(390.83, 658.8, 394.5, 655.16, 408.0, 655.0);
		shape0.curveTo(408.0, 655.0, 426.0, 655.0, 426.0, 655.0);
		shape0.curveTo(438.41, 655.0, 449.56, 654.18, 451.0, 669.85);
		shape0.curveTo(451.0, 669.85, 439.09, 669.85, 439.09, 669.85);
		shape0.curveTo(435.69, 668.9, 435.07, 666.96, 430.99, 666.11);
		shape0.curveTo(430.99, 666.11, 416.0, 666.11, 416.0, 666.11);
		shape0.curveTo(413.67, 666.01, 410.26, 665.88, 408.24, 667.17);
		shape0.curveTo(405.23, 669.11, 405.96, 672.11, 409.07, 673.4);
		shape0.curveTo(410.99, 674.2, 414.82, 673.94, 417.0, 674.04);
		shape0.curveTo(417.0, 674.04, 432.0, 675.0, 432.0, 675.0);
		shape0.curveTo(436.88, 675.01, 442.9, 674.98, 446.78, 678.43);
		shape0.curveTo(452.97, 683.93, 452.39, 698.4, 445.89, 703.49);
		shape0.curveTo(442.36, 706.26, 438.22, 705.99, 434.0, 706.0);
		shape0.curveTo(434.0, 706.0, 405.0, 706.0, 405.0, 706.0);
		shape0.curveTo(400.88, 705.95, 397.39, 705.79, 394.23, 702.72);
		shape0.curveTo(390.66, 699.27, 391.01, 695.54, 391.0, 691.14);
		shape0.curveTo(391.0, 691.14, 401.83, 691.14, 401.83, 691.14);
		shape0.closePath();
		shape0.moveTo(466.83, 691.14);
		shape0.curveTo(472.16, 692.62, 469.98, 695.94, 483.0, 696.0);
		shape0.curveTo(486.91, 696.02, 494.46, 696.03, 497.92, 694.44);
		shape0.curveTo(502.06, 692.55, 502.0, 688.52, 497.92, 686.74);
		shape0.curveTo(495.78, 685.82, 492.34, 686.08, 490.0, 685.91);
		shape0.curveTo(490.0, 685.91, 478.0, 685.0, 478.0, 685.0);
		shape0.curveTo(467.25, 684.98, 456.19, 685.88, 456.0, 672.0);
		shape0.curveTo(455.83, 658.8, 459.5, 655.16, 473.0, 655.0);
		shape0.curveTo(473.0, 655.0, 491.0, 655.0, 491.0, 655.0);
		shape0.curveTo(503.41, 655.0, 514.56, 654.18, 516.0, 669.85);
		shape0.curveTo(516.0, 669.85, 504.09, 669.85, 504.09, 669.85);
		shape0.curveTo(500.69, 668.9, 500.07, 666.96, 495.99, 666.11);
		shape0.curveTo(495.99, 666.11, 481.0, 666.11, 481.0, 666.11);
		shape0.curveTo(478.67, 666.01, 475.26, 665.88, 473.24, 667.17);
		shape0.curveTo(470.23, 669.11, 470.96, 672.11, 474.07, 673.4);
		shape0.curveTo(475.99, 674.2, 479.82, 673.94, 482.0, 674.04);
		shape0.curveTo(482.0, 674.04, 497.0, 675.0, 497.0, 675.0);
		shape0.curveTo(501.88, 675.01, 507.9, 674.98, 511.78, 678.43);
		shape0.curveTo(517.97, 683.93, 517.39, 698.4, 510.89, 703.49);
		shape0.curveTo(507.36, 706.26, 503.22, 705.99, 499.0, 706.0);
		shape0.curveTo(499.0, 706.0, 470.0, 706.0, 470.0, 706.0);
		shape0.curveTo(465.88, 705.95, 462.39, 705.79, 459.23, 702.72);
		shape0.curveTo(455.66, 699.27, 456.01, 695.54, 456.0, 691.14);
		shape0.curveTo(456.0, 691.14, 466.83, 691.14, 466.83, 691.14);
		shape0.closePath();
		shape0.moveTo(537.0, 655.0);
		shape0.curveTo(537.0, 655.0, 537.0, 706.0, 537.0, 706.0);
		shape0.curveTo(537.0, 706.0, 524.0, 706.0, 524.0, 706.0);
		shape0.curveTo(524.0, 706.0, 524.0, 655.0, 524.0, 655.0);
		shape0.curveTo(524.0, 655.0, 537.0, 655.0, 537.0, 655.0);
		shape0.closePath();
		shape0.moveTo(555.83, 691.14);
		shape0.curveTo(561.16, 692.62, 558.98, 695.94, 572.0, 696.0);
		shape0.curveTo(575.91, 696.02, 583.46, 696.03, 586.92, 694.44);
		shape0.curveTo(591.06, 692.55, 591.0, 688.52, 586.92, 686.74);
		shape0.curveTo(584.78, 685.82, 581.34, 686.08, 579.0, 685.91);
		shape0.curveTo(579.0, 685.91, 567.0, 685.0, 567.0, 685.0);
		shape0.curveTo(556.25, 684.98, 545.19, 685.88, 545.0, 672.0);
		shape0.curveTo(544.83, 658.8, 548.5, 655.16, 562.0, 655.0);
		shape0.curveTo(562.0, 655.0, 580.0, 655.0, 580.0, 655.0);
		shape0.curveTo(592.41, 655.0, 603.56, 654.18, 605.0, 669.85);
		shape0.curveTo(605.0, 669.85, 593.09, 669.85, 593.09, 669.85);
		shape0.curveTo(589.69, 668.9, 589.07, 666.96, 584.99, 666.11);
		shape0.curveTo(584.99, 666.11, 570.0, 666.11, 570.0, 666.11);
		shape0.curveTo(567.67, 666.01, 564.26, 665.88, 562.24, 667.17);
		shape0.curveTo(559.23, 669.11, 559.96, 672.11, 563.07, 673.4);
		shape0.curveTo(564.99, 674.2, 568.82, 673.94, 571.0, 674.04);
		shape0.curveTo(571.0, 674.04, 586.0, 675.0, 586.0, 675.0);
		shape0.curveTo(590.88, 675.01, 596.9, 674.98, 600.78, 678.43);
		shape0.curveTo(606.97, 683.93, 606.39, 698.4, 599.89, 703.49);
		shape0.curveTo(596.36, 706.26, 592.22, 705.99, 588.0, 706.0);
		shape0.curveTo(588.0, 706.0, 559.0, 706.0, 559.0, 706.0);
		shape0.curveTo(554.88, 705.95, 551.39, 705.79, 548.23, 702.72);
		shape0.curveTo(544.66, 699.27, 545.01, 695.54, 545.0, 691.14);
		shape0.curveTo(545.0, 691.14, 555.83, 691.14, 555.83, 691.14);
		shape0.closePath();
		shape0.moveTo(345.0, 684.0);
		shape0.curveTo(345.0, 684.0, 363.0, 684.0, 363.0, 684.0);
		shape0.curveTo(363.0, 684.0, 355.0, 666.0, 355.0, 666.0);
		shape0.curveTo(355.0, 666.0, 345.0, 684.0, 345.0, 684.0);
		shape0.closePath();
		g.setPaint(new Color(0, 0, 0, 255));
		g.fill(shape0);
		g.setStroke(new BasicStroke(1.0f, 0, 0, 4.0f, null, 0.0f));
		g.draw(shape0);
	}

	private void paintShapeNode_0_0_1(Graphics2D g)
	{
		GeneralPath shape1 = new GeneralPath();
		shape1.moveTo(632.0, 210.0);
		shape1.curveTo(632.0, 210.0, 632.0, 274.0, 632.0, 274.0);
		shape1.curveTo(632.0, 274.0, 471.0, 274.0, 471.0, 274.0);
		shape1.curveTo(471.0, 274.0, 512.0, 316.0, 512.0, 316.0);
		shape1.curveTo(512.0, 316.0, 591.0, 395.0, 591.0, 395.0);
		shape1.curveTo(591.0, 395.0, 620.0, 424.0, 620.0, 424.0);
		shape1.curveTo(627.55, 431.55, 631.98, 433.03, 632.0, 444.0);
		shape1.curveTo(632.0, 444.0, 632.0, 525.0, 632.0, 525.0);
		shape1.curveTo(632.0, 525.0, 612.0, 506.0, 612.0, 506.0);
		shape1.curveTo(612.0, 506.0, 577.0, 471.0, 577.0, 471.0);
		shape1.curveTo(577.0, 471.0, 470.0, 364.0, 470.0, 364.0);
		shape1.curveTo(470.0, 364.0, 442.0, 336.0, 442.0, 336.0);
		shape1.curveTo(442.0, 336.0, 424.0, 319.0, 424.0, 319.0);
		shape1.curveTo(424.0, 319.0, 424.0, 483.0, 424.0, 483.0);
		shape1.curveTo(424.0, 483.0, 360.0, 483.0, 360.0, 483.0);
		shape1.curveTo(360.0, 483.0, 360.0, 210.0, 360.0, 210.0);
		shape1.curveTo(360.0, 210.0, 632.0, 210.0, 632.0, 210.0);
		shape1.closePath();
		g.setPaint(new Color(0, 102, 145, 255));
		g.fill(shape1);
		g.setPaint(new Color(0, 0, 0, 255));
		g.draw(shape1);
	}

	private void paintShapeNode_0_0_2(Graphics2D g)
	{
		GeneralPath shape2 = new GeneralPath();
		shape2.moveTo(167.0, 332.0);
		shape2.curveTo(167.0, 332.0, 156.0, 342.98, 156.0, 342.98);
		shape2.curveTo(156.0, 342.98, 150.0, 346.8, 150.0, 346.8);
		shape2.curveTo(150.0, 346.8, 143.0, 342.0, 143.0, 342.0);
		shape2.curveTo(143.0, 342.0, 128.0, 327.0, 128.0, 327.0);
		shape2.curveTo(128.0, 327.0, 115.0, 314.0, 115.0, 314.0);
		shape2.curveTo(115.0, 314.0, 110.2, 307.0, 110.2, 307.0);
		shape2.curveTo(110.2, 307.0, 115.0, 300.0, 115.0, 300.0);
		shape2.curveTo(115.0, 300.0, 131.0, 284.0, 131.0, 284.0);
		shape2.curveTo(131.0, 284.0, 194.0, 221.0, 194.0, 221.0);
		shape2.curveTo(194.0, 221.0, 281.0, 134.0, 281.0, 134.0);
		shape2.curveTo(281.0, 134.0, 292.01, 122.0, 292.01, 122.0);
		shape2.curveTo(292.01, 122.0, 340.0, 74.0, 340.0, 74.0);
		shape2.curveTo(340.0, 74.0, 363.0, 51.04, 363.0, 51.04);
		shape2.curveTo(363.0, 51.04, 375.0, 40.0, 375.0, 40.0);
		shape2.curveTo(375.0, 40.0, 390.0, 26.6, 390.0, 26.6);
		shape2.curveTo(390.0, 26.6, 397.0, 26.0, 397.0, 26.0);
		shape2.curveTo(397.0, 26.0, 472.0, 26.0, 472.0, 26.0);
		shape2.curveTo(472.0, 26.0, 454.0, 45.0, 454.0, 45.0);
		shape2.curveTo(454.0, 45.0, 423.0, 76.0, 423.0, 76.0);
		shape2.curveTo(423.0, 76.0, 327.0, 172.0, 327.0, 172.0);
		shape2.curveTo(327.0, 172.0, 292.0, 207.0, 292.0, 207.0);
		shape2.curveTo(292.0, 207.0, 278.6, 222.0, 278.6, 222.0);
		shape2.curveTo(278.6, 222.0, 278.0, 229.0, 278.0, 229.0);
		shape2.curveTo(278.0, 229.0, 278.0, 566.0, 278.0, 566.0);
		shape2.curveTo(278.0, 566.0, 339.0, 566.0, 339.0, 566.0);
		shape2.curveTo(339.0, 566.0, 356.0, 565.0, 356.0, 565.0);
		shape2.curveTo(356.0, 565.0, 632.0, 565.0, 632.0, 565.0);
		shape2.curveTo(632.0, 565.0, 632.0, 629.0, 632.0, 629.0);
		shape2.curveTo(632.0, 629.0, 213.0, 629.0, 213.0, 629.0);
		shape2.curveTo(213.0, 629.0, 213.0, 286.0, 213.0, 286.0);
		shape2.curveTo(213.0, 286.0, 167.0, 332.0, 167.0, 332.0);
		shape2.closePath();
		g.setPaint(new Color(79, 91, 107, 255));
		g.fill(shape2);
		g.setPaint(new Color(0, 0, 0, 255));
		g.draw(shape2);
	}

	private void paintCanvasGraphicsNode_0_0(Graphics2D g)
	{
		// _0_0_0
		AffineTransform trans_0_0_0 = g.getTransform();
		g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
		paintShapeNode_0_0_0(g);
		g.setTransform(trans_0_0_0);
		// _0_0_1
		AffineTransform trans_0_0_1 = g.getTransform();
		g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
		paintShapeNode_0_0_1(g);
		g.setTransform(trans_0_0_1);
		// _0_0_2
		AffineTransform trans_0_0_2 = g.getTransform();
		g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f));
		paintShapeNode_0_0_2(g);
		g.setTransform(trans_0_0_2);
	}

	private void paintRootGraphicsNode_0(Graphics2D g)
	{
		// _0_0
		g.setComposite(AlphaComposite.getInstance(3, 1.0f * origAlpha));
		AffineTransform trans_0_0 = g.getTransform();
		g.transform(new AffineTransform(1.3328747749328613f, 0.0f, 0.0f,
				1.3328747749328613f, 0.20289243705337867f, -0.0f));
		paintCanvasGraphicsNode_0_0(g);
		g.setTransform(trans_0_0);
	}

	public int getOrigX()
	{
		return 147;
	}

	public int getOrigY()
	{
		return 34;
	}

	public int getOrigWidth()
	{
		return 697;
	}

	public int getOrigHeight()
	{
		return 908;
	}

	/**
	 * The current width of this resizable icon.
	 */
	int width;
	/**
	 * The current height of this resizable icon.
	 */
	int height;

	/**
	 * Creates a new transcoded SVG image.
	 */
	public MASSISIcon()
	{
		this.width = getOrigWidth();
		this.height = getOrigHeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconHeight()
	 */
	@Override
	public int getIconHeight()
	{
		return height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconWidth()
	 */
	@Override
	public int getIconWidth()
	{
		return width;
	}

	/*
	 * Set the dimension of the icon.
	 */
	public void setDimension(Dimension newDimension)
	{
		this.width = newDimension.width;
		this.height = newDimension.height;
	}

	public MASSISIcon setDimensionAndReturn(Dimension newDimension)
	{
		this.width = newDimension.width;
		this.height = newDimension.height;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics,
	 * int, int)
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.translate(x, y);

		double coef1 = (double) this.width / (double) getOrigWidth();
		double coef2 = (double) this.height / (double) getOrigHeight();
		double coef = Math.min(coef1, coef2);
		g2d.scale(coef, coef);
		paint(g2d);
		g2d.dispose();
	}
}
