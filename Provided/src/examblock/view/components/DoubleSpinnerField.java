package examblock.view.components;

import examblock.view.components.DoubleTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DoubleSpinnerField extends DoubleTextField {

    private JButton incrementButton;
    private JButton decrementButton;
    private double step = 0.1;
    private double min = Double.NEGATIVE_INFINITY;
    private double max = Double.POSITIVE_INFINITY;

    public DoubleSpinnerField() {
        super();
        setDouble(0.0); // Default value
        setColumns(5);  // Reasonable default size

        incrementButton = new JButton("+");
        decrementButton = new JButton("-");

        incrementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double newValue = Math.min(getDouble() + step, max);
                setDouble(newValue);
            }
        });

        decrementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double newValue = Math.max(getDouble() - step, min);
                setDouble(newValue);
            }
        });

        // Layout components
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        buttonPanel.add(incrementButton);
        buttonPanel.add(decrementButton);
        this.add(buttonPanel, BorderLayout.EAST);
    }

    @Override
    public void setDouble(double value) {
        value = Math.max(min, Math.min(max, value));
        super.setDouble(Math.round(value * 10.0) / 10.0); // 1 decimal place
    }

    public void setMinimum(double min) {
        this.min = min;
        if (getDouble() < min) {
            setDouble(min);
        }
    }

    public void setMaximum(double max) {
        this.max = max;
        if (getDouble() > max) {
            setDouble(max);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension textSize = super.getPreferredSize();
        int width = textSize.width + 30; // room for spinner buttons
        int height = textSize.height;
        return new Dimension(width, height);
    }
}
