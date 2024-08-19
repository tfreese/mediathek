package de.freese.player.iir.designer;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.TextEvent;
import java.io.Serial;

import javax.swing.JFrame;

public final class IIRFilterDesign extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    public static void main(final String[] args) {
        final IIRFilterDesign iirFilterDesign = new IIRFilterDesign();
        iirFilterDesign.init();

        iirFilterDesign.setTitle("IIRFilterDesign");
        iirFilterDesign.setSize(800, 600);
        iirFilterDesign.setLocationRelativeTo(null);
        iirFilterDesign.setVisible(true);
    }

    Button btnCoeffs = new Button();
    Button btnDesign = new Button();
    Button btnPoleZero = new Button();
    Button btnResponse = new Button();
    CardLayout cardLayout1 = new CardLayout();
    Checkbox cbBandPass = new Checkbox();
    Checkbox cbButterworth = new Checkbox();
    Checkbox cbChebyshev = new Checkbox();
    Checkbox cbHighPass = new Checkbox();
    Checkbox cbLowPass = new Checkbox();
    CheckboxGroup cbgFilterType = new CheckboxGroup();
    CheckboxGroup cbgPrototype = new CheckboxGroup();
    Choice chMinGain = new Choice();
    IIRFilter filter = new IIRFilter();
    String filterType;
    GraphPlot frPlot = new GraphPlot();
    int freqPoints = 250;   // number of points in FR plot
    float[] gain = new float[freqPoints + 1];
    boolean isStandalone = false;
    Label lblFilterType = new Label();
    Label lblHz = new Label();
    Label lblMaxOrder = new Label();
    Label lblMinGain = new Label();
    Label lblMinGainUnit = new Label();
    Label lblOrder = new Label();
    Label lblPassband = new Label();
    Label lblPrototype = new Label();
    Label lblRipple = new Label();
    Label lblRippleUnit = new Label();
    Label lblTo = new Label();
    int maxOrder = 16;      // maximum filter order
    int order;
    Panel pnlButtons1 = new Panel();
    Panel pnlButtons2 = new Panel();
    Panel pnlCoeffs = new Panel();
    Panel pnlControls = new Panel();
    Panel pnlDisplay = new Panel();
    Panel pnlFRPlot = new Panel();
    Panel pnlFilterType = new Panel();
    Panel pnlLeftPanel = new Panel();
    Panel pnlMinGain = new Panel();
    Panel pnlOrder = new Panel();
    Panel pnlPZPlot = new Panel();
    Panel pnlPassband = new Panel();
    Panel pnlPrototype = new Panel();
    Panel pnlRightPanel = new Panel();
    Panel pnlRipple = new Panel();
    String prototype;
    PoleZeroPlot pzPlot = new PoleZeroPlot();
    float rate = 8000.0F;   // fixed sampling rate
    TextField tfFreq1 = new TextField();
    TextField tfFreq2 = new TextField();
    TextField tfOrder = new TextField();
    TextField tfRipple = new TextField();
    TextArea txtCoeffs = new TextArea();

    public void init() {
        final Font f = new Font("Dialog", 0, 11);
        this.setSize(new Dimension(600, 400));

        lblFilterType.setFont(f);
        lblFilterType.setText("Filter type:");
        cbLowPass.setFont(f);
        cbLowPass.setLabel("LP");
        cbLowPass.setCheckboxGroup(cbgFilterType);
        cbLowPass.addItemListener(this::cbLowPassItemStateChanged);
        cbBandPass.setFont(f);
        cbBandPass.setLabel("BP");
        cbBandPass.setCheckboxGroup(cbgFilterType);
        cbBandPass.addItemListener(this::cbBandPassItemStateChanged);
        cbHighPass.setFont(f);
        cbHighPass.setLabel("HP");
        cbHighPass.setCheckboxGroup(cbgFilterType);
        cbHighPass.addItemListener(this::cbHighPassItemStateChanged);
        cbLowPass.setState(true);

        lblPrototype.setFont(f);
        lblPrototype.setText("Prototype:");
        cbButterworth.setFont(f);
        cbButterworth.setLabel("Butterworth");
        cbButterworth.setCheckboxGroup(cbgPrototype);
        cbButterworth.addItemListener(this::cbButterworthItemStateChanged);
        cbChebyshev.setFont(f);
        cbChebyshev.setLabel("Chebyshev");
        cbChebyshev.setCheckboxGroup(cbgPrototype);
        cbChebyshev.addItemListener(this::cbChebyshevItemStateChanged);
        cbButterworth.setState(true);

        lblOrder.setFont(f);
        lblOrder.setText("Filter order:");
        tfOrder.setFont(f);
        tfOrder.setText("1");
        tfOrder.setColumns(3);
        tfOrder.addTextListener(this::tfOrderTextValueChanged);
        lblMaxOrder.setFont(new Font("Dialog", Font.ITALIC, 11));
        lblMaxOrder.setText("(max " + maxOrder + ")");

        txtCoeffs.setText("Filter Coefficients\n\n");
        btnCoeffs.addActionListener(this::btnCoeffsActionPerformed);
        btnCoeffs.setEnabled(false);
        btnCoeffs.setLabel("Coefficients");
        pnlCoeffs.setLayout(new BorderLayout());

        final FlowLayout flowLayout8 = new FlowLayout();
        flowLayout8.setAlignment(FlowLayout.LEFT);
        pnlButtons2.setLayout(flowLayout8);

        pnlFRPlot.setLayout(new BorderLayout());
        frPlot.setPlotStyle(GraphPlot.SPECTRUM);
        frPlot.setTracePlot(true);
        frPlot.setLogScale(true);
        pzPlot.setSize(new Dimension(300, 268));
        frPlot.setBgColor(Color.lightGray);
        frPlot.setPlotColor(Color.blue);
        frPlot.setAxisColor(Color.darkGray);
        frPlot.setGridColor(Color.darkGray);
        pnlFRPlot.add(frPlot, BorderLayout.CENTER);
        pnlCoeffs.add(txtCoeffs, BorderLayout.CENTER);
        pnlPZPlot.setLayout(new BorderLayout());
        pnlPZPlot.add(pzPlot, BorderLayout.CENTER);
        pnlDisplay.setLayout(cardLayout1);
        pnlDisplay.add(pnlPZPlot, "PZPlot");
        pnlDisplay.add(pnlFRPlot, "FRPlot");
        pnlDisplay.add(pnlCoeffs, "Coeffs");
        lblPassband.setFont(f);
        lblPassband.setText("Passband:");
        tfFreq1.setFont(f);
        tfFreq1.setText("0");
        tfFreq1.setColumns(5);
        tfFreq1.addTextListener(this::tfFreq1TextValueChanged);
        lblTo.setFont(f);
        lblTo.setAlignment(Label.CENTER);
        lblTo.setText("to");
        tfFreq2.setFont(f);
        tfFreq2.setText("1000");
        tfFreq2.setColumns(5);
        tfFreq2.addTextListener(this::tfFreq2TextValueChanged);
        lblHz.setFont(f);
        lblHz.setText("Hz");

        lblRipple.setEnabled(false);
        lblRipple.setFont(f);
        lblRipple.setText("Passband ripple:");
        tfRipple.setEnabled(false);
        tfRipple.setFont(f);
        tfRipple.setText("1.0");
        tfRipple.setColumns(3);
        tfRipple.addTextListener(this::tfRippleTextValueChanged);
        lblRippleUnit.setEnabled(false);
        lblRippleUnit.setFont(f);
        lblRippleUnit.setText("dB");
        btnResponse.setEnabled(false);
        btnResponse.setFont(f);
        btnResponse.setLabel("Frequency response");
        btnResponse.addActionListener(this::btnResponseActionPerformed);
        btnDesign.setFont(f);
        btnDesign.setLabel("Design");
        lblMinGain.setEnabled(false);
        lblMinGain.setFont(f);
        lblMinGain.setText("Minimum plot gain:");
        lblMinGainUnit.setEnabled(false);
        lblMinGainUnit.setFont(f);
        lblMinGainUnit.setText("dB");
        btnPoleZero.setLabel("Poles/Zeros");
        btnPoleZero.setEnabled(false);
        btnPoleZero.setFont(f);
        btnPoleZero.addActionListener(this::btnPoleZeroActionPerformed);
        chMinGain.addItem("-10");
        chMinGain.addItem("-50");
        chMinGain.addItem("-100");
        chMinGain.addItem("-200");
        chMinGain.select("-100");
        chMinGain.addItemListener(this::chMinGainItemStateChanged);
        chMinGain.setEnabled(false);
        chMinGain.setFont(f);
        btnDesign.addActionListener(this::btnDesignActionPerformed);

        final FlowLayout flowLayout7 = new FlowLayout();
        flowLayout7.setAlignment(FlowLayout.LEFT);
        pnlButtons1.setLayout(flowLayout7);

        final FlowLayout flowLayout6 = new FlowLayout();
        pnlMinGain.setLayout(flowLayout6);

        final FlowLayout flowLayout5 = new FlowLayout();
        flowLayout5.setAlignment(FlowLayout.LEFT);
        pnlRipple.setLayout(flowLayout5);

        final FlowLayout flowLayout4 = new FlowLayout();
        flowLayout4.setAlignment(FlowLayout.LEFT);
        pnlPassband.setLayout(flowLayout4);

        final FlowLayout flowLayout3 = new FlowLayout();
        flowLayout3.setAlignment(FlowLayout.LEFT);
        pnlOrder.setLayout(flowLayout3);

        final FlowLayout flowLayout2 = new FlowLayout();
        flowLayout2.setAlignment(FlowLayout.LEFT);
        pnlPrototype.setLayout(flowLayout2);

        final FlowLayout flowLayout1 = new FlowLayout();
        flowLayout1.setAlignment(FlowLayout.LEFT);
        pnlFilterType.setLayout(flowLayout1);

        final GridLayout gridLayout3 = new GridLayout();
        gridLayout3.setRows(4);
        gridLayout3.setColumns(1);
        pnlRightPanel.setLayout(gridLayout3);

        final GridLayout gridLayout2 = new GridLayout();
        gridLayout2.setColumns(1);
        gridLayout2.setRows(4);
        pnlLeftPanel.setLayout(gridLayout2);

        final GridLayout gridLayout1 = new GridLayout();
        gridLayout1.setColumns(2);
        pnlControls.setLayout(gridLayout1);

        this.setLayout(new BorderLayout());

        // Gain frequency response plot
        this.add(pnlDisplay, BorderLayout.CENTER);
        this.add(pnlControls, BorderLayout.SOUTH);
        pnlControls.add(pnlLeftPanel, null);
        pnlLeftPanel.add(pnlFilterType, null);
        pnlFilterType.add(lblFilterType, null);
        pnlFilterType.add(cbLowPass, null);
        pnlFilterType.add(cbBandPass, null);
        pnlFilterType.add(cbHighPass, null);
        pnlLeftPanel.add(pnlPrototype, null);
        pnlPrototype.add(lblPrototype, null);
        pnlPrototype.add(cbButterworth, null);
        pnlPrototype.add(cbChebyshev, null);
        pnlLeftPanel.add(pnlOrder, null);
        pnlOrder.add(lblOrder, null);
        pnlOrder.add(tfOrder, null);
        pnlOrder.add(lblMaxOrder, null);
        pnlLeftPanel.add(pnlPassband, null);
        pnlPassband.add(lblPassband, null);
        pnlPassband.add(tfFreq1, null);
        pnlPassband.add(lblTo, null);
        pnlPassband.add(tfFreq2, null);
        pnlPassband.add(lblHz, null);
        pnlControls.add(pnlRightPanel, null);
        pnlRightPanel.add(pnlRipple, null);
        pnlRipple.add(lblRipple, null);
        pnlRipple.add(tfRipple, null);
        pnlRipple.add(lblRippleUnit, null);
        pnlRightPanel.add(pnlMinGain, null);
        pnlMinGain.add(lblMinGain, null);
        pnlMinGain.add(chMinGain, null);
        pnlMinGain.add(lblMinGainUnit, null);
        pnlRightPanel.add(pnlButtons1, null);
        pnlButtons1.add(btnDesign, null);
        pnlButtons1.add(btnPoleZero, null);
        pnlRightPanel.add(pnlButtons2, null);
        pnlButtons2.add(btnResponse, null);
        pnlButtons2.add(btnCoeffs, null);
    }

    void btnCoeffsActionPerformed(final ActionEvent event) {
        listCoeffs();
    }

    void btnDesignActionPerformed(final ActionEvent event) {
        designFilter();
    }

    void btnPoleZeroActionPerformed(final ActionEvent event) {
        setMinGainState(false);
        plotPolesAndZeros();
        cardLayout1.show(pnlDisplay, "PZPlot");
    }

    void btnResponseActionPerformed(final ActionEvent event) {
        setMinGainState(true);
        plotResponse();
        btnCoeffs.setEnabled(true);
        cardLayout1.show(pnlDisplay, "FRPlot");
    }

    void cbBandPassItemStateChanged(final ItemEvent event) {
        if (cbBandPass.getState()) {
            tfFreq1.setText("");
            tfFreq2.setText("");
            tfFreq1.setEditable(true);
            tfFreq2.setEditable(true);
            btnResponse.setEnabled(false);
            btnPoleZero.setEnabled(false);
            btnCoeffs.setEnabled(false);
        }
    }

    void cbButterworthItemStateChanged(final ItemEvent event) {
        setRippleState(cbChebyshev.getState());
        btnResponse.setEnabled(false);
        btnPoleZero.setEnabled(false);
        btnPoleZero.setEnabled(false);
    }

    void cbChebyshevItemStateChanged(final ItemEvent event) {
        setRippleState(cbChebyshev.getState());
        btnResponse.setEnabled(false);
        btnPoleZero.setEnabled(false);
        btnPoleZero.setEnabled(false);
    }

    void cbHighPassItemStateChanged(final ItemEvent e) {
        if (cbHighPass.getState()) {
            tfFreq1.setText("");
            tfFreq2.setText(String.valueOf(Math.round(rate / 2F)));
            tfFreq1.setEditable(true);
            tfFreq2.setEditable(false);
            btnResponse.setEnabled(false);
            btnPoleZero.setEnabled(false);
            btnCoeffs.setEnabled(false);
        }
    }

    void cbLowPassItemStateChanged(final ItemEvent event) {
        if (cbLowPass.getState()) {
            tfFreq1.setText("0");
            tfFreq2.setText("");
            tfFreq1.setEditable(false);
            tfFreq2.setEditable(true);
            btnResponse.setEnabled(false);
            btnPoleZero.setEnabled(false);
            btnCoeffs.setEnabled(false);
        }
    }

    void chMinGainItemStateChanged(final ItemEvent event) {
        if (btnResponse.isEnabled()) {
            plotResponse();
        }
    }

    void designFilter() {
        order = Integer.parseInt(tfOrder.getText());

        if (order > maxOrder) {
            // showStatus("Filter order too high (max. " + maxOrder + ")");
            System.out.println("Filter order too high (max. " + maxOrder + ")");
        }
        else {
            filterType = cbgFilterType.getSelectedCheckbox().getLabel();

            if ((filterType.equals("BP")) && ((order % 2) != 0)) {
                // showStatus("Filter order must be even for BP filter");
                System.out.println("Filter order must be even for BP filter");
            }
            else {
                filter.setFilterType(filterType);
                prototype = cbgPrototype.getSelectedCheckbox().getLabel();
                filter.setPrototype(prototype);
                filter.setRate(rate);
                filter.setOrder(order);
                filter.setFreq1(Float.valueOf(tfFreq1.getText()));
                filter.setFreq2(Float.valueOf(tfFreq2.getText()));

                if (cbChebyshev.getState()) {
                    filter.setRipple(Float.valueOf(tfRipple.getText()));
                }

                filter.design();
                btnResponse.setEnabled(true);
                btnPoleZero.setEnabled(true);
            }
        }
    }

    void listCoeffs() {
        txtCoeffs.setText(prototype + " IIR filter\n\n");
        txtCoeffs.append("Filter type: " + filterType + "\n");
        txtCoeffs.append("Passband: " + tfFreq1.getText() + " - " + tfFreq2.getText() + " Hz\n");

        if (cbChebyshev.getState()) {
            txtCoeffs.append("Passband ripple: " + tfRipple.getText() + " dB\n");
        }

        txtCoeffs.append("Order: " + order + "\n\n");
        txtCoeffs.append("Coefficients\n\n");

        for (int i = 0; i <= order; i++) {
            txtCoeffs.append("a[" + i + "] = "
                    + filter.getACoeff(i)
                    + "     \tb[" + i + "] = "
                    + filter.getBCoeff(i) + "\n");
        }

        cardLayout1.show(pnlDisplay, "Coeffs");
    }

    /**
     * Pole and zero locations.<br>
     * NB poles and zeros have indices 1 .. order.
     */
    void plotPolesAndZeros() {

        final float[] pReal = new float[order + 1];
        final float[] pImag = new float[order + 1];
        final float[] z = new float[order + 1];

        for (int i = 1; i <= order; i++) {
            pReal[i] = filter.getPReal(i);
            pImag[i] = filter.getPImag(i);
            z[i] = filter.getZero(i);
        }

        pzPlot.setPolesAndZeros(pReal, pImag, z);
    }

    void plotResponse() {
        filter.setFreqPoints(freqPoints);
        gain = filter.filterGain();
        frPlot.setYmax(minPlotGain());
        frPlot.setPlotValues(gain);
    }

    void tfFreq1TextValueChanged(final TextEvent event) {
        btnResponse.setEnabled(false);
        btnPoleZero.setEnabled(false);
        btnCoeffs.setEnabled(false);
    }

    void tfFreq2TextValueChanged(final TextEvent event) {
        btnPoleZero.setEnabled(false);
        btnResponse.setEnabled(false);
        btnCoeffs.setEnabled(false);
    }

    void tfOrderTextValueChanged(final TextEvent e) {
        btnPoleZero.setEnabled(false);
        btnResponse.setEnabled(false);
        btnCoeffs.setEnabled(false);
    }

    void tfRippleTextValueChanged(final TextEvent event) {
        btnPoleZero.setEnabled(false);
        btnResponse.setEnabled(false);
        btnCoeffs.setEnabled(false);
    }

    private float minPlotGain() {
        return Math.abs(Float.valueOf(chMinGain.getSelectedItem()).floatValue());
    }

    private void setMinGainState(final boolean b) {
        lblMinGain.setEnabled(b);
        chMinGain.setEnabled(b);
        lblMinGainUnit.setEnabled(b);
    }

    private void setRippleState(final boolean b) {
        lblRipple.setEnabled(b);
        tfRipple.setEnabled(b);
        lblRippleUnit.setEnabled(b);
    }
}
