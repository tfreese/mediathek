package de.freese.player.equalizer.iir.designer;

/**
 * IIR filter design code based on a Pascal program listed in "Digital Signal Processing with Computer Applications" by P. Lynn and W. Fuerst (Prentice Hall).
 */
class IIRFilter {

    public static final int BP = 3;
    public static final int BUTTERWORTH = 4;
    public static final int CHEBYSHEV = 5;
    public static final int HP = 2;
    public static final int LP = 1;

    private double[] aCoeff;
    private double[] bCoeff;
    private float fN;
    private int filterType;
    private float fp1;
    private float fp2;
    private int freqPoints;
    private int order;
    private double[] pImag;
    private double[] pReal;
    private int prototype;
    private float rate;
    private float ripple;
    private double[] z;

    public IIRFilter() {
        super();

        prototype = BUTTERWORTH;
        filterType = LP;
        order = 1;
        ripple = 1.0F;
        rate = 8000.0F;
        fN = 0.5F * rate;
        fp1 = 0.0F;
        fp2 = 1000.0F;
    }

    public void design() {
        aCoeff = new double[order + 1];
        bCoeff = new double[order + 1];
        final double[] newA = new double[order + 1];
        final double[] newB = new double[order + 1];

        locatePolesAndZeros(); // find filter poles and zeros

        // compute filter coefficients from pole/zero values
        aCoeff[0] = 1.0D;
        bCoeff[0] = 1.0D;

        for (int i = 1; i <= order; i++) {
            aCoeff[i] = 0.0D;
            bCoeff[i] = 0.0D;
        }

        int k = 0;
        final int n = order;
        final int pairs = n / 2;

        if (odd(order)) {
            // first subfilter is first order
            aCoeff[1] = -z[1];
            bCoeff[1] = -pReal[1];
            k = 1;
        }

        for (int p = 1; p <= pairs; p++) {
            final int m = 2 * p - 1 + k;
            final double alpha1 = -(z[m] + z[m + 1]);
            final double alpha2 = z[m] * z[m + 1];
            final double beta1 = -2.0 * pReal[m];
            final double beta2 = sqr(pReal[m]) + sqr(pImag[m]);

            newA[1] = aCoeff[1] + alpha1 * aCoeff[0];
            newB[1] = bCoeff[1] + beta1 * bCoeff[0];

            for (int i = 2; i <= n; i++) {
                newA[i] = aCoeff[i] + alpha1 * aCoeff[i - 1] + alpha2 * aCoeff[i - 2];
                newB[i] = bCoeff[i] + beta1 * bCoeff[i - 1] + beta2 * bCoeff[i - 2];
            }

            for (int i = 1; i <= n; i++) {
                aCoeff[i] = newA[i];
                bCoeff[i] = newB[i];
            }
        }
    }

    public float[] filterGain() {
        // filter gain at uniform frequency intervals
        final float[] g = new float[freqPoints + 1];
        double theta;
        double s;
        double c;
        double sac;
        double sas;
        double sbc;
        double sbs;
        float gMax = -100.0F;
        final float sc = 10.0F / (float) Math.log(10.0F);
        final double t = Math.PI / freqPoints;

        for (int i = 0; i <= freqPoints; i++) {
            theta = i * t;

            if (i == 0) {
                theta = Math.PI * 0.0001D;
            }

            if (i == freqPoints) {
                theta = Math.PI * 0.9999D;
            }

            sac = 0.0F;
            sas = 0.0F;
            sbc = 0.0F;
            sbs = 0.0F;

            for (int k = 0; k <= order; k++) {
                c = Math.cos(k * theta);
                s = Math.sin(k * theta);
                sac += c * aCoeff[k];
                sas += s * aCoeff[k];
                sbc += c * bCoeff[k];
                sbs += s * bCoeff[k];
            }

            g[i] = sc * (float) Math.log((sqr(sac) + sqr(sas)) / (sqr(sbc) + sqr(sbs)));
            gMax = Math.max(gMax, g[i]);
        }

        // normalise to 0 dB maximum gain
        for (int i = 0; i <= freqPoints; i++) {
            g[i] -= gMax;
        }

        // normalise numerator (a) coefficients
        final float normFactor = (float) Math.pow(10.0D, -0.05D * gMax);

        for (int i = 0; i <= order; i++) {
            aCoeff[i] *= normFactor;
        }

        return g;
    }

    /**
     * IIR filter numerator coefficient with index i.
     */
    public float getACoeff(final int i) {
        return (float) aCoeff[i];
    }

    /**
     * IIR filter denominator coefficient with index i.
     */
    public float getBCoeff(final int i) {
        return (float) bCoeff[i];
    }

    public int getFilterType() {
        return filterType;
    }

    public float getFreq1() {
        return fp1;
    }

    public float getFreq2() {
        return fp2;
    }

    public int getFreqPoints() {
        return freqPoints;
    }

    public int getOrder() {
        return order;
    }

    /**
     * Imaginary part of filter pole with index i.
     */
    public float getPImag(final int i) {
        return (float) pImag[i];
    }

    /**
     * Real part of filter pole with index i.
     */
    public float getPReal(final int i) {
        return (float) pReal[i];
    }

    public int getPrototype() {
        return prototype;
    }

    public float getRate() {
        return rate;
    }

    public float getRipple() {
        return ripple;
    }

    /**
     * Filter zero with index i.
     */
    public float getZero(final int i) {
        return (float) z[i];
    }

    public void setFilterType(final String ft) {
        if (ft.equals("LP")) {
            setFilterType(LP);
        }

        if (ft.equals("BP")) {
            setFilterType(BP);
        }

        if (ft.equals("HP")) {
            setFilterType(HP);
        }
    }

    public void setFilterType(final int ft) {
        filterType = ft;

        if ((filterType == BP) && odd(order)) {
            order++;
        }
    }

    public void setFreq1(final float fp1) {
        this.fp1 = fp1;
    }

    public void setFreq2(final float fp2) {
        this.fp2 = fp2;
    }

    public void setFreqPoints(final int f) {
        freqPoints = f;
    }

    public void setOrder(final int n) {
        order = Math.abs(n);

        if ((filterType == BP) && odd(order)) {
            order++;
        }
    }

    public void setPrototype(final String p) {
        if (p.equals("Butterworth")) {
            setPrototype(BUTTERWORTH);
        }

        if (p.equals("Chebyshev")) {
            setPrototype(CHEBYSHEV);
        }
    }

    public void setPrototype(final int p) {
        prototype = p;
    }

    public void setRate(final float r) {
        rate = r;
        fN = 0.5F * rate;
    }

    public void setRipple(final float r) {
        ripple = r;
    }

    boolean odd(final int n) {
        // returns true if n odd
        return (n % 2) != 0;
    }

    double sqr(final double x) {
        return x * x;
    }

    float sqr(final float x) {
        return x * x;
    }

    private void locatePolesAndZeros() {
        // determines poles and zeros of IIR filter
        // based on bilinear transform method
        pReal = new double[order + 1];
        pImag = new double[order + 1];
        z = new double[order + 1];
        final double ln10 = Math.log(10.0D);

        for (int k = 1; k <= order; k++) {
            pReal[k] = 0.0D;
            pImag[k] = 0.0D;
        }

        // Butterworth, Chebyshev parameters
        int n = order;

        if (filterType == BP) {
            n = n / 2;
        }

        final int ir = n % 2;
        final int n1 = n + ir;
        final int n2 = (3 * n + ir) / 2 - 1;
        final double f1;

        switch (filterType) {
            case LP:
                f1 = fp2;
                break;
            case HP:
                f1 = fN - fp1;
                break;
            case BP:
                f1 = fp2 - fp1;
                break;
            default:
                f1 = 0.0;
        }

        final double tanw1 = Math.tan(0.5D * Math.PI * f1 / fN);
        final double tansqw1 = sqr(tanw1);

        // Real and Imaginary parts of low-pass poles
        double t;
        double a = 1.0D;
        double r = 1.0D;
        double i = 1.0D;

        for (int k = n1; k <= n2; k++) {
            t = 0.5D * (2 * k + 1 - ir) * Math.PI / (double) n;

            switch (prototype) {
                case BUTTERWORTH:
                    final double b3 = 1.0D - 2.0D * tanw1 * Math.cos(t) + tansqw1;
                    r = (1.0D - tansqw1) / b3;
                    i = 2.0D * tanw1 * Math.sin(t) / b3;
                    break;
                case CHEBYSHEV:
                    final double d = 1.0D - Math.exp(-0.05D * ripple * ln10);
                    final double e = 1.0D / Math.sqrt(1.0D / sqr(1.0D - d) - 1.0D);
                    final double x = Math.pow(Math.sqrt(e * e + 1.0D) + e, 1.0D / (double) n);
                    a = 0.5D * (x - 1.0D / x);

                    final double b = 0.5D * (x + 1.0D / x);
                    final double c3 = a * tanw1 * Math.cos(t);
                    final double c4 = b * tanw1 * Math.sin(t);
                    final double c5 = sqr(1.0D - c3) + sqr(c4);
                    r = 2.0D * (1.0D - c3) / c5 - 1.0D;
                    i = 2.0D * c4 / c5;
                    break;
            }

            final int m = 2 * (n2 - k) + 1;
            pReal[m + ir] = r;
            pImag[m + ir] = Math.abs(i);
            pReal[m + ir + 1] = r;
            pImag[m + ir + 1] = -Math.abs(i);
        }

        if (odd(n)) {
            if (prototype == BUTTERWORTH) {
                r = (1.0D - tansqw1) / (1.0D + 2.0D * tanw1 + tansqw1);
            }

            if (prototype == CHEBYSHEV) {
                r = 2.0D / (1.0D + a * tanw1) - 1.0D;
            }

            pReal[1] = r;
            pImag[1] = 0.0D;
        }

        switch (filterType) {
            case LP:
                for (int m = 1; m <= n; m++) {
                    z[m] = -1.0D;
                }
                break;
            case HP:
                // low-pass to high-pass transformation
                for (int m = 1; m <= n; m++) {
                    pReal[m] = -pReal[m];
                    z[m] = 1.0D;
                }
                break;
            case BP:
                // low-pass to bandpass transformation
                for (int m = 1; m <= n; m++) {
                    z[m] = 1.0D;
                    z[m + n] = -1.0D;
                }
                final double f4 = 0.5D * Math.PI * fp1 / fN;
                final double f5 = 0.5D * Math.PI * fp2 / fN;

                // check this bit ... needs value for gp to adjust critical freqs
                // if (prototype == BUTTERWORTH) {
                //     f4 = f4 / Math.exp(0.5 * Math.log(gp) / n);
                //     f5 = fN - (fN - f5) / Math.exp(0.5 * Math.log(gp) / n);
                // }

                final double aa = Math.cos(f4 + f5) / Math.cos(f5 - f4);
                double aR;
                double aI;
                double h1;
                double h2;
                double p1R;
                double p2R;
                double p1I;
                double p2I;

                for (int m1 = 0; m1 <= (order - 1) / 2; m1++) {
                    final int m = 1 + 2 * m1;
                    aR = pReal[m];
                    aI = pImag[m];

                    if (Math.abs(aI) < 0.0001D) {
                        h1 = 0.5D * aa * (1.0D + aR);
                        h2 = sqr(h1) - aR;

                        if (h2 > 0.0D) {
                            p1R = h1 + Math.sqrt(h2);
                            p2R = h1 - Math.sqrt(h2);
                            p1I = 0.0D;
                            p2I = 0.0D;
                        }
                        else {
                            p1R = h1;
                            p2R = h1;
                            p1I = Math.sqrt(Math.abs(h2));
                            p2I = -p1I;
                        }
                    }
                    else {
                        final double fR = aa * 0.5D * (1.0D + aR);
                        final double fI = aa * 0.5D * aI;
                        final double gR = sqr(fR) - sqr(fI) - aR;
                        final double gI = 2D * fR * fI - aI;
                        final double sR = Math.sqrt(0.5D * Math.abs(gR + Math.sqrt(sqr(gR) + sqr(gI))));
                        final double sI = gI / (2.0D * sR);
                        p1R = fR + sR;
                        p1I = fI + sI;
                        p2R = fR - sR;
                        p2I = fI - sI;
                    }

                    pReal[m] = p1R;
                    pReal[m + 1] = p2R;
                    pImag[m] = p1I;
                    pImag[m + 1] = p2I;
                } // end of m1 for-loop

                if (odd(n)) {
                    pReal[2] = pReal[n + 1];
                    pImag[2] = pImag[n + 1];
                }

                for (int k = n; k >= 1; k--) {
                    final int m = 2 * k - 1;
                    pReal[m] = pReal[k];
                    pReal[m + 1] = pReal[k];
                    pImag[m] = Math.abs(pImag[k]);
                    pImag[m + 1] = -Math.abs(pImag[k]);
                }
                break;
            default:
        }
    }
}
