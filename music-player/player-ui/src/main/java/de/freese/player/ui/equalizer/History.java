/*
 *  21.04.2004 Original verion. davagin@udm.ru.
 *-----------------------------------------------------------------------
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
package de.freese.player.ui.equalizer;

/**
 * Structure for storing history data of equalizer.
 */
public class History {
    /**
     * X data: x[n], x[n-1], x[n-2]
     */
    private final double[] x = new double[3];
    /**
     * Y data: y[n], y[n-1], y[n-2]
     */
    private final double[] y = new double[3];

    public void clear() {
        for (int i = 0; i < 3; i++) {
            x[i] = 0D;
            y[i] = 0D;
        }
    }

    public double getX(final int index) {
        return x[index];
    }

    public double getY(final int index) {
        return y[index];
    }

    public void setX(final int index, final double value) {
        x[index] = value;
    }

    public void setY(final int index, final double value) {
        y[index] = value;
    }
}
