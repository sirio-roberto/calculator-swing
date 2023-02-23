package calculator;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Stack;

public class Calculator extends JFrame {


    String[] buttonTexts = new String[] {"( )", "CE", "C", "Del",
                                        "x\u00B2", "x\u02B8", String.valueOf('\u221A'), String.valueOf('\u00F7'),
                                        "7", "8", "9", String.valueOf('\u00D7'),
                                        "4", "5", "6", String.valueOf('\u002B'),
                                        "1", "2", "3", String.valueOf('\u002D'),
                                        String.valueOf('\u00B1'), "0", ".", "=" };
    String[] buttonNames = new String[] {"Parentheses", "CE", "Clear", "Delete",
                                        "PowerTwo", "PowerY", "SquareRoot", "Divide",
                                        "Seven", "Eight", "Nine", "Multiply",
                                        "Four", "Five", "Six", "Add",
                                        "One", "Two", "Three", "Subtract",
                                        "PlusMinus", "Zero", "Dot", "Equals" };

    final java.util.List<String> grayButtons = java.util.List.of("Parentheses", "CE", "Clear", "Delete",
                                    "PowerTwo", "PowerY", "SquareRoot", "Divide", "Multiply", "Add", "Subtract");

    public Calculator() {
        setTitle(getClass().getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLayout(new BorderLayout());
        setBackground(Color.GRAY);

        JLabel equationLabel = new JLabel("");
        equationLabel.setFont(equationLabel.getFont().deriveFont(14f));
        equationLabel.setBackground(Color.GRAY);
        equationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        equationLabel.setName("EquationLabel");

        JLabel resultLabel = new JLabel("0");
        resultLabel.setName("ResultLabel");
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        resultLabel.setFont(equationLabel.getFont().deriveFont(Font.BOLD, 44));

        JPanel resultPanel = new JPanel(new GridLayout(2,1, 10, 10));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        resultPanel.add(resultLabel);
        resultPanel.add(equationLabel);
        add(resultPanel, BorderLayout.PAGE_START);

        JPanel buttonPanel = new JPanel(new GridLayout(7, 4, 2, 2));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        addButtons(buttonPanel, equationLabel, resultLabel);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void addButtons(JPanel buttonPanel, JLabel equationLabel, JLabel resultLabel) {
        for (int i = 0; i < buttonTexts.length; i++) {
            JButton jButton = new JButton(buttonTexts[i]);
            jButton.setFont(jButton.getFont().deriveFont(18f));
            jButton.setFocusPainted(false);
            jButton.setName(buttonNames[i]);
            jButton.setBorder(null);
            if (buttonNames[i].equals("Equals")) {
                jButton.setBackground(Color.LIGHT_GRAY);
            } else if(grayButtons.contains(buttonNames[i])) {
                jButton.setBackground(new Color(220,220,220));
            } else {
                jButton.setBackground(Color.WHITE);
            }

            jButton.addActionListener(e -> {
                if (jButton.getText().matches("[0-9.]")) {
                    equationLabel.setText(equationLabel.getText() + jButton.getText());
                } else if (jButton.getText().matches("[\u00F7\u00D7\u002B\u002D]")){
                    if (!equationLabel.getText().isBlank()) {
                        equationLabel.setText(getFormattedDots(equationLabel.getText()));
                        if ("\u00F7\u00D7\u002B\u002D".contains(getLastCharacter(equationLabel.getText()))) {
                            equationLabel.setText(removeLastCharacter(equationLabel.getText()) + jButton.getText());
                        }
                        else {
                            equationLabel.setText(equationLabel.getText() + jButton.getText());
                        }
                    }
                } else if (jButton.getText().equals("( )")) {
                    if (isBalancedParentheses(equationLabel.getText())
                            || hasMoreLeftThanRightParentheses(equationLabel.getText())) {
                        if (equationLabel.getText().isEmpty()
                                || "\u00F7\u00D7\u002B\u002D(".contains(getLastCharacter(equationLabel.getText()))) {
                            equationLabel.setText(equationLabel.getText() + "(");
                        } else if (hasMoreLeftThanRightParentheses(equationLabel.getText())) {
                            equationLabel.setText(equationLabel.getText() + ")");
                        }
                    }
                } else if (jButton.getName().equals("SquareRoot")) {
                    equationLabel.setText(equationLabel.getText() + "√(");
                } else if (jButton.getName().equals("PowerTwo")) {
                    equationLabel.setText(equationLabel.getText() + "^(2)");
                } else if (jButton.getName().equals("PowerY")) {
                    equationLabel.setText(equationLabel.getText() + "^(");
                } else if (jButton.getName().equals("PlusMinus")) {
                    if (equationLabel.getText().length() < 2) {
                        equationLabel.setText("(-" + equationLabel.getText());
                    } else if (equationLabel.getText().length() >= 2) {
                        if (equationLabel.getText().charAt(0) == '(' && equationLabel.getText().charAt(1) == '-') {
                            equationLabel.setText(equationLabel.getText().substring(2));
                        } else {
                            equationLabel.setText("(-" + equationLabel.getText());
                        }
                    }
                } else if (jButton.getText().matches("Del")) {
                    if (!equationLabel.getText().isBlank()) {
                        equationLabel.setText(removeLastCharacter(equationLabel.getText()));
                    }
                } else if (jButton.getText().matches("CE?")) {
                    equationLabel.setText("");
                    resultLabel.setText("0");
                } else {
                    // equal
                    if (!equationLabel.getText().isBlank()) {
                        if ("\u00F7\u00D7\u002B\u002D".contains(getLastCharacter(equationLabel.getText()))
                        || equationLabel.getText().contains("-√")) {
                            equationLabel.setForeground(Color.RED.darker());
                        } else {
                            try {
                                equationLabel.setForeground(Color.BLACK);
                                String postfixExpression = convertInfixToPostfix(equationLabel.getText());
                                // add missing spaces
                                postfixExpression = addAdditionalSpace(postfixExpression);
                                postfixExpression = postfixExpression.replaceAll("\\s+", " ").trim();

                                Double result = calculateUsingPostfix(postfixExpression);
                                DecimalFormat df = new DecimalFormat("#.##########");
                                resultLabel.setText(df.format(result));
                            } catch (ArithmeticException ex) {
                                equationLabel.setForeground(Color.RED.darker());
                            }
                        }
                    }

                }
            });

            buttonPanel.add(jButton);
        }
    }

    private boolean hasMoreLeftThanRightParentheses(String equationText) {
        int leftParentheses = 0;
        int rightParentheses = 0;
        for (char c: equationText.toCharArray()) {
            if (c == '(') {
                leftParentheses++;
            } else if (c == ')') {
                rightParentheses++;
            }
        }
        return leftParentheses > rightParentheses;
    }

    private boolean isBalancedParentheses(String equationText) {
        int leftParentheses = 0;
        int rightParentheses = 0;
        for (char c: equationText.toCharArray()) {
            if (c == '(') {
                leftParentheses++;
            } else if (c == ')') {
                rightParentheses++;
            }
        }
        return leftParentheses == rightParentheses;
    }

    private String getFormattedDots(String text) {
        String formattedText = text.replaceFirst("(?<![0-9])\\.", "0.");
        formattedText = formattedText.replaceFirst("\\.(?![0-9])", ".0");
        return formattedText;
    }

    private CharSequence getLastCharacter(String text) {
        return text.substring(text.length() - 1);
    }

    private String removeLastCharacter(String text) {
        return text.substring(0, text.length() - 1);
    }

    private String addAdditionalSpace(String postfixExpression) {
        StringBuilder sb = new StringBuilder();
        for (char c: postfixExpression.toCharArray()) {
            if (!checkIfOperand(c)) {
                sb.append(" ").append(c).append(" ");
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Double calculateUsingPostfix(String postfixExpression) {
        double operand1, operand2;
        Stack<Double> stack = new Stack<>();
        String[] postfixArray = postfixExpression.split(" ");
        for (String s : postfixArray) {
            if ("\u00F7\u00D7\u002B\u002D^√".contains(s)) {
                operand2 = stack.pop();
                if (!stack.isEmpty() && !s.equals("√")) {
                    operand1 = stack.pop();
                } else {
                    operand1 = 0;
                }
                stack.push(calculateOperation(s, operand1, operand2));
            } else {
                stack.push(Double.parseDouble(s));
            }
        }
        return stack.pop();
    }

    private Double calculateOperation(String operation, Double operand1, Double operand2) {
        double result = 0;
        switch (operation) {
            case "^" -> result = Math.pow(operand1, operand2);
            case "\u221A" -> {
                if (operand2 < 0) {
                    throw new ArithmeticException("Square root of negative values is not real");
                }
                result = Math.sqrt(operand2);
            }
            case "\u002D" -> result = operand1 - operand2;
            case "\u002B" -> result = operand1 + operand2;
            case "\u00D7" -> result = operand1 * operand2;
            case "\u00F7" ->  {
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero not allowed");
                }
                result = operand1 / operand2;
            }
        }
        return result;
    }

    private String convertInfixToPostfix(String infixText) {
        int i;
        Stack<Character> s = new Stack<>();
        StringBuilder result = new StringBuilder();

        for (i = 0; i < infixText.length(); ++i)
        {
            // Here we are checking is the character we scanned is operand or not
            // and this adding to output.
            if (checkIfOperand(infixText.charAt(i))) {
                result.append(infixText.charAt(i));
            } else {
                result.append(" ");

                // Here, if we scan the character (, we need to push it to the stack.
            if (infixText.charAt(i) == '(')
                    s.push(infixText.charAt(i));

                    // Here, if we scan character is an ), we need to pop and print from the stack
                    // do this until an ( is encountered in the stack.
                else if (infixText.charAt(i) == ')') {
                    while (!s.empty() && s.peek() != '(') {
                        result.append(s.peek());
                        s.pop();
                    }
                    if (!s.empty() && s.peek() != '(')
                        return "";
                    else
                        s.pop();
                } else // if an operator
                {
                    while (!s.empty() && precedence(infixText.charAt(i)) <= precedence(s.peek())) {
                        result.append(s.peek());
                        s.pop();
                    }
                    s.push(infixText.charAt(i));
                }
            }
        }

        // Once all initial expression characters are traversed
        // adding all left elements from stack to exp
        while (!s.empty()){
            result.append(s.peek());
            s.pop();
        }
        return result.toString();
    }

    private boolean checkIfOperand(char ch) {
        return ".0123456789".contains(String.valueOf(ch));
    }

    // Function to compare precedence
    // If we return larger value means higher precedence
    private int precedence(char ch) {
        return switch (ch) {
            case '\u002B', '\u002D' -> 1;
            case '\u00D7', '\u00F7' -> 2;
            case '^', '\u221A' -> 3;
            default -> -1;
        };
    }
}
