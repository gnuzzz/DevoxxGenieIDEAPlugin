package com.devoxx.genie.ui;

import com.devoxx.genie.service.SettingsStateService;
import com.devoxx.genie.ui.topic.AppTopics;
import com.devoxx.genie.ui.util.DoubleConverter;
import com.devoxx.genie.ui.util.NotificationUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import static com.intellij.openapi.options.Configurable.isFieldModified;

public class DevoxxGenieSettingsManager implements Configurable {

    private final DoubleConverter doubleConverter;

    private JTextField ollamaUrlField;
    private JTextField lmstudioUrlField;
    private JTextField gpt4allUrlField;
    private JTextField janUrlField;

    private JPasswordField openAiKeyField;
    private JPasswordField mistralKeyField;
    private JPasswordField anthropicKeyField;
    private JPasswordField groqKeyField;
    private JPasswordField deepInfraKeyField;
    private JPasswordField geminiKeyField;

    private JPasswordField tavilySearchKeyField;
    private JPasswordField googleSearchKeyField;
    private JPasswordField googleCSIKeyField;

    private JFormattedTextField temperatureField;
    private JFormattedTextField topPField;

    private JFormattedTextField timeoutField;
    private JFormattedTextField retryField;
    private JFormattedTextField chatMemorySizeField;

    private JCheckBox streamModeCheckBox;
    private JCheckBox hideSearchCheckBox;

    private JCheckBox astModeCheckBox;
    private JCheckBox astParentClassCheckBox;
    private JCheckBox astReferenceFieldCheckBox;
    private JCheckBox astReferenceClassesCheckBox;

    private JTextField testPromptField;
    private JTextField explainPromptField;
    private JTextField reviewPromptField;
    private JTextField customPromptField;
    private JTextField maxOutputTokensField;

    public DevoxxGenieSettingsManager() {
        doubleConverter = new DoubleConverter();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Devoxx Genie Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        SettingsStateService settings = SettingsStateService.getInstance();

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Helper method to add a text field with label
        setTitle("Local Large Language Models", settingsPanel, gbc);

        ollamaUrlField = addFieldWithLinkButton(settingsPanel, gbc, "Ollama URL:", settings.getOllamaModelUrl(), "https://ollama.com/");
        lmstudioUrlField = addFieldWithLinkButton(settingsPanel, gbc, "LMStudio URL:", settings.getLmstudioModelUrl(), "https://lmstudio.ai/");
        gpt4allUrlField = addFieldWithLinkButton(settingsPanel, gbc, "GPT4All URL:", settings.getGpt4allModelUrl(), "https://gpt4all.io/");
        janUrlField = addFieldWithLinkButton(settingsPanel, gbc, "Jan URL:", settings.getJanModelUrl(), "https://jan.ai/download");

        setTitle("Cloud based Large Language Models", settingsPanel, gbc);

        openAiKeyField = addFieldWithLabelPasswordAndLinkButton(settingsPanel, gbc, "OpenAI API Key :", settings.getOpenAIKey(), "https://platform.openai.com/api-keys");
        mistralKeyField = addFieldWithLabelPasswordAndLinkButton(settingsPanel, gbc, "Mistral API Key :", settings.getMistralKey(), "https://console.mistral.ai/api-keys");
        anthropicKeyField = addFieldWithLabelPasswordAndLinkButton(settingsPanel, gbc, "Anthropic API Key :", settings.getAnthropicKey(), "https://console.anthropic.com/settings/keys");
        groqKeyField = addFieldWithLabelPasswordAndLinkButton(settingsPanel, gbc, "Groq API Key :", settings.getGroqKey(), "https://console.groq.com/keys");
        deepInfraKeyField = addFieldWithLabelPasswordAndLinkButton(settingsPanel, gbc, "DeepInfra API Key :", settings.getDeepInfraKey(), "https://deepinfra.com/dash/api_keys");
        geminiKeyField = addFieldWithLabelPasswordAndLinkButton(settingsPanel, gbc, "Gemini API Key :", settings.getGeminiKey(), "https://aistudio.google.com/app/apikey");

        setTitle("Search Providers", settingsPanel, gbc);
        hideSearchCheckBox = addCheckBoxWithLabel(settingsPanel, gbc, "Hide Search Providers", false, "", false);
        tavilySearchKeyField = addFieldWithLabelPasswordAndLinkButton(settingsPanel, gbc, "Tavily Web Search API Key :", settings.getTavilySearchKey(), "https://app.tavily.com/home");
        googleSearchKeyField = addFieldWithLabelPasswordAndLinkButton(settingsPanel, gbc, "Google Web Search API Key :", settings.getGoogleSearchKey(), "https://developers.google.com/custom-search/docs/paid_element#api_key");
        googleCSIKeyField = addFieldWithLabelPasswordAndLinkButton(settingsPanel, gbc, "Google Custom Search Engine ID :", settings.getGoogleCSIKey(), "https://programmablesearchengine.google.com/controlpanel/create");

        setTitle("LLM Parameters", settingsPanel, gbc);

        chatMemorySizeField = addFormattedFieldWithLabel(settingsPanel, gbc, "Chat memory size:", settings.getTemperature());
        temperatureField = addFormattedFieldWithLabel(settingsPanel, gbc, "Temperature:", settings.getTemperature());
        topPField = addFormattedFieldWithLabel(settingsPanel, gbc, "Top-P:", settings.getTopP());
        maxOutputTokensField = addTextFieldWithLabel(settingsPanel, gbc, "Maximum output tokens :", settings.getMaxOutputTokens());
        timeoutField = addFormattedFieldWithLabel(settingsPanel, gbc, "Timeout (in secs):", settings.getTimeout());
        retryField = addFormattedFieldWithLabel(settingsPanel, gbc, "Maximum retries :", settings.getMaxRetries());
        streamModeCheckBox = addCheckBoxWithLabel(settingsPanel, gbc, "Enable Stream Mode (Beta)", settings.getStreamMode(),
            "Streaming response does not support (yet) copy code buttons to clipboard", false);

        setTitle("Abstract Syntax Tree Config", settingsPanel, gbc);
        astModeCheckBox = addCheckBoxWithLabel(settingsPanel, gbc, "Enable AST Mode (Beta)", settings.getAstMode(),
            "Enable Abstract Syntax Tree mode for code generation, results in including related classes in the prompt.", false);
        astParentClassCheckBox = addCheckBoxWithLabel(settingsPanel, gbc, "Include project parent class(es)", settings.getAstParentClass(), "", true);
        astReferenceClassesCheckBox = addCheckBoxWithLabel(settingsPanel, gbc, "Include class references", settings.getAstClassReference(), "", true);
        astReferenceFieldCheckBox = addCheckBoxWithLabel(settingsPanel, gbc, "Include field references", settings.getAstFieldReference(), "", true);

        astModeCheckBox.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            astParentClassCheckBox.setEnabled(selected);
            astReferenceClassesCheckBox.setEnabled(selected);
            astReferenceFieldCheckBox.setEnabled(selected);
        });

        hideSearchCheckBox.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            tavilySearchKeyField.setEnabled(!selected);
            googleSearchKeyField.setEnabled(!selected);
            googleCSIKeyField.setEnabled(!selected);
        });

        setTitle("Predefined Command Prompts", settingsPanel, gbc);

        testPromptField = addTextFieldWithLabel(settingsPanel, gbc, "Test prompt :", settings.getTestPrompt());
        explainPromptField = addTextFieldWithLabel(settingsPanel, gbc, "Explain prompt :", settings.getExplainPrompt());
        reviewPromptField = addTextFieldWithLabel(settingsPanel, gbc, "Review prompt :", settings.getReviewPrompt());
        customPromptField = addTextFieldWithLabel(settingsPanel, gbc, "Custom prompt :", settings.getCustomPrompt());
        return settingsPanel;
    }

    /**
     * Set the title of the settings panel
     *
     * @param title         the title
     * @param settingsPanel the settings panel
     * @param gbc           the grid bag constraints
     */
    private void setTitle(String title,
                          @NotNull JPanel settingsPanel,
                          @NotNull GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel(title);

        gbc.insets = JBUI.insets(10, 0);
        settingsPanel.add(titleLabel, gbc);

        // Reset the insets for the next component
        gbc.insets = JBUI.emptyInsets();

        // Add vertical spacing below the title
        gbc.weighty = 1.0; // Allow the empty space to expand vertically
        settingsPanel.add(new JLabel(), gbc);

        // Reset the constraints for the next component
        gbc.weighty = 0.0;
        resetGbc(gbc);
    }

    /**
     * Add a text field with label
     *
     * @param panel the panel
     * @param gbc   the gridbag constraints
     * @param label the label
     * @param value the value
     * @return the text field
     */
    private @NotNull JTextField addTextFieldWithLabel(@NotNull JPanel panel,
                                                      GridBagConstraints gbc,
                                                      String label,
                                                      String value) {
        panel.add(new JLabel(label), gbc);
        gbc.gridx++;
        JTextField textField = new JTextField(value);
        panel.add(textField, gbc);
        resetGbc(gbc);
        return textField;
    }

    /**
     * Add a field with label and a link button
     *
     * @param panel the panel
     * @param gbc   the gridbag constraints
     * @param label the label
     * @param value the value
     * @param url   the url
     * @return the text field
     */
    private @NotNull JTextField addFieldWithLinkButton(@NotNull JPanel panel,
                                                       GridBagConstraints gbc,
                                                       String label,
                                                       String value,
                                                       String url) {
        String wwwEmoji = "\uD83D\uDD17";
        JButton btnApiKey = createButton(wwwEmoji, "Download from", url);
        panel.add(new JLabel(label), gbc);
        gbc.gridx++;
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        JTextField textField = new JTextField(value);
        jPanel.add(textField, BorderLayout.CENTER);
        jPanel.add(btnApiKey, BorderLayout.WEST);
        panel.add(jPanel, gbc);
        resetGbc(gbc);
        return textField;
    }

    /**
     * Add field with label, password and link button
     *
     * @param panel the panel
     * @param gbc   the gridbag constraints
     * @param label the label
     * @param value the value
     * @param url   the url
     * @return the password field
     */
    private @NotNull JPasswordField addFieldWithLabelPasswordAndLinkButton(@NotNull JPanel panel,
                                                                           GridBagConstraints gbc,
                                                                           String label,
                                                                           String value,
                                                                           String url) {
        String keyEmoji = "\uD83D\uDD11";
        JButton btnApiKey = createButton(keyEmoji, "Get your API Key from ", url);
        panel.add(new JLabel(label), gbc);
        gbc.gridx++;
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        JPasswordField passwordField = new JPasswordField(value);
        passwordField.setEchoChar('*');
        jPanel.add(passwordField, BorderLayout.CENTER);
        jPanel.add(btnApiKey, BorderLayout.WEST);
        panel.add(jPanel, gbc);
        resetGbc(gbc);
        return passwordField;
    }

    /**
     * Create a button with emoji and tooltip message and open the URL in the browser
     *
     * @param emoji      the emoji to use in the button
     * @param toolTipMsg the tooltip message
     * @param url        the url to open when clicked
     * @return the created button
     */
    private @NotNull JButton createButton(String emoji,
                                          String toolTipMsg,
                                          String url) {
        JButton btnApiKey = new JButton(emoji);
        btnApiKey.setToolTipText(toolTipMsg + " " + url);
        btnApiKey.addActionListener(e -> {
            try {
                BrowserUtil.open(url);
            } catch (Exception ex) {
                Project project = ProjectManager.getInstance().getOpenProjects()[0];
                NotificationUtil.sendNotification(project, "Error: Unable to open the link");
            }
        });
        return btnApiKey;
    }

    /**
     * Add a formatted field with label
     *
     * @param panel the panel
     * @param gbc   the gridbag constraints
     * @param label the label
     * @param value the value
     * @return the formatted field
     */
    private @NotNull JFormattedTextField addFormattedFieldWithLabel(@NotNull JPanel panel,
                                                                    GridBagConstraints gbc,
                                                                    String label,
                                                                    Number value) {
        panel.add(new JLabel(label), gbc);
        gbc.gridx++;
        JFormattedTextField formattedField = new JFormattedTextField();
        setValue(formattedField, value);
        panel.add(formattedField, gbc);
        resetGbc(gbc);
        return formattedField;
    }

    /**
     * Add a formatted field with label
     *
     * @param panel the panel
     * @param gbc   the grid bag constraints
     * @param label the label
     * @param value the value
     * @param tooltip the tooltip
     * @return the formatted field
     */
    private @NotNull JCheckBox addCheckBoxWithLabel(@NotNull JPanel panel,
                                                    GridBagConstraints gbc,
                                                    String label,
                                                    Boolean value,
                                                    @NotNull String tooltip,
                                                    boolean labelInNextColumn) {
        JCheckBox checkBox = new JCheckBox();
        if (!tooltip.isEmpty()) {
            checkBox.setToolTipText(tooltip);
        }
        if (value != null) {
            checkBox.setSelected(value);
        }

        if (labelInNextColumn) {
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.add(new JLabel(label), BorderLayout.CENTER);
            jPanel.add(checkBox, BorderLayout.WEST);
            gbc.gridx++;
            panel.add(jPanel, gbc);
        } else {
            panel.add(new JLabel(label), gbc);
            gbc.gridx++;
            panel.add(checkBox, gbc);
        }

        resetGbc(gbc);
        return checkBox;
    }

    private void resetGbc(@NotNull GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
    }

    @Override
    public boolean isModified() {
        SettingsStateService settings = SettingsStateService.getInstance();

        boolean isModified = isFieldModified(ollamaUrlField, settings.getOllamaModelUrl());
        isModified |= isFieldModified(lmstudioUrlField, settings.getLmstudioModelUrl());
        isModified |= isFieldModified(gpt4allUrlField, settings.getGpt4allModelUrl());
        isModified |= isFieldModified(janUrlField, settings.getJanModelUrl());
        isModified |= isFieldModified(temperatureField, Objects.requireNonNull(doubleConverter.toString(settings.getTemperature())));
        isModified |= isFieldModified(topPField, Objects.requireNonNull(doubleConverter.toString(settings.getTopP())));
        isModified |= isFieldModified(timeoutField, settings.getTimeout());
        isModified |= isFieldModified(maxOutputTokensField, settings.getMaxOutputTokens());
        isModified |= isFieldModified(retryField, settings.getMaxRetries());
        isModified |= isFieldModified(chatMemorySizeField, settings.getChatMemorySize());
        isModified |= isFieldModified(testPromptField, settings.getTestPrompt());
        isModified |= isFieldModified(explainPromptField, settings.getExplainPrompt());
        isModified |= isFieldModified(reviewPromptField, settings.getReviewPrompt());
        isModified |= isFieldModified(customPromptField, settings.getCustomPrompt());
        isModified |= isFieldModified(openAiKeyField, settings.getOpenAIKey());
        isModified |= isFieldModified(mistralKeyField, settings.getMistralKey());
        isModified |= isFieldModified(anthropicKeyField, settings.getAnthropicKey());
        isModified |= isFieldModified(groqKeyField, settings.getGroqKey());
        isModified |= isFieldModified(deepInfraKeyField, settings.getDeepInfraKey());
        isModified |= isFieldModified(geminiKeyField, settings.getGeminiKey());

        isModified |= !settings.getHideSearchButtonsFlag().equals(hideSearchCheckBox.isSelected());
        isModified |= isFieldModified(tavilySearchKeyField, settings.getTavilySearchKey());
        isModified |= isFieldModified(googleSearchKeyField, settings.getGoogleSearchKey());
        isModified |= isFieldModified(googleCSIKeyField, settings.getGoogleCSIKey());

        isModified |= !settings.getStreamMode().equals(streamModeCheckBox.isSelected());
        isModified |= !settings.getAstMode().equals(astModeCheckBox.isSelected());
        isModified |= !settings.getAstParentClass().equals(astParentClassCheckBox.isSelected());
        isModified |= !settings.getAstClassReference().equals(astReferenceClassesCheckBox.isSelected());
        isModified |= !settings.getAstFieldReference().equals(astReferenceFieldCheckBox.isSelected());

        return isModified;
    }

    @Override
    public void apply() {
        SettingsStateService settings = SettingsStateService.getInstance();

        boolean apiKeyModified = false;
        apiKeyModified |= updateSettingIfModified(openAiKeyField, settings.getOpenAIKey(), settings::setOpenAIKey);
        apiKeyModified |= updateSettingIfModified(mistralKeyField, settings.getMistralKey(), settings::setMistralKey);
        apiKeyModified |= updateSettingIfModified(anthropicKeyField, settings.getAnthropicKey(), settings::setAnthropicKey);
        apiKeyModified |= updateSettingIfModified(groqKeyField, settings.getGroqKey(), settings::setGroqKey);
        apiKeyModified |= updateSettingIfModified(deepInfraKeyField, settings.getDeepInfraKey(), settings::setDeepInfraKey);
        apiKeyModified |= updateSettingIfModified(geminiKeyField, settings.getGeminiKey(), settings::setGeminiKey);

        apiKeyModified |= updateSettingIfModified(hideSearchCheckBox, settings.getHideSearchButtonsFlag(), value ->
            settings.setHideSearchButtonsFlag(Boolean.parseBoolean(value))
        );
        apiKeyModified |= updateSettingIfModified(tavilySearchKeyField, settings.getTavilySearchKey(), settings::setTavilySearchKey);
        apiKeyModified |= updateSettingIfModified(googleSearchKeyField, settings.getGoogleSearchKey(), settings::setGoogleSearchKey);
        apiKeyModified |= updateSettingIfModified(googleCSIKeyField, settings.getGoogleCSIKey(), settings::setGoogleCSIKey);

        if (apiKeyModified) {
            // Only notify the listener if an API key has changed, so we can refresh the LLM providers list in the UI
            notifySettingsChanged();
        }
        updateSettingIfModified(hideSearchCheckBox, settings.getHideSearchButtonsFlag(), value -> {
            settings.setHideSearchButtonsFlag(Boolean.parseBoolean(value));
        });

        updateSettingIfModified(ollamaUrlField, settings.getOllamaModelUrl(), settings::setOllamaModelUrl);
        updateSettingIfModified(lmstudioUrlField, settings.getLmstudioModelUrl(), settings::setLmstudioModelUrl);
        updateSettingIfModified(gpt4allUrlField, settings.getGpt4allModelUrl(), settings::setGpt4allModelUrl);
        updateSettingIfModified(janUrlField, settings.getJanModelUrl(), settings::setJanModelUrl);
        updateSettingIfModified(temperatureField, doubleConverter.toString(settings.getTemperature()), value -> settings.setTemperature(doubleConverter.fromString(value)));
        updateSettingIfModified(topPField, doubleConverter.toString(settings.getTopP()), value -> settings.setTopP(doubleConverter.fromString(value)));
        updateSettingIfModified(timeoutField, settings.getTimeout(), value -> settings.setTimeout(safeCastToInteger(value)));
        updateSettingIfModified(retryField, settings.getMaxRetries(), value -> settings.setMaxRetries(safeCastToInteger(value)));
        updateSettingIfModified(maxOutputTokensField, settings.getMaxOutputTokens(), settings::setMaxOutputTokens);
        updateSettingIfModified(testPromptField, settings.getTestPrompt(), settings::setTestPrompt);
        updateSettingIfModified(explainPromptField, settings.getExplainPrompt(), settings::setExplainPrompt);
        updateSettingIfModified(reviewPromptField, settings.getReviewPrompt(), settings::setReviewPrompt);
        updateSettingIfModified(customPromptField, settings.getCustomPrompt(), settings::setCustomPrompt);
        updateSettingIfModified(streamModeCheckBox, settings.getStreamMode(), value -> settings.setStreamMode(Boolean.parseBoolean(value)));

        updateSettingIfModified(astModeCheckBox, settings.getAstMode(), value -> settings.setAstMode(Boolean.parseBoolean(value)));
        updateSettingIfModified(astParentClassCheckBox, settings.getAstParentClass(), value -> settings.setAstParentClass(Boolean.parseBoolean(value)));
        updateSettingIfModified(astReferenceClassesCheckBox, settings.getAstClassReference(), value -> settings.setAstClassReference(Boolean.parseBoolean(value)));
        updateSettingIfModified(astReferenceFieldCheckBox, settings.getAstFieldReference(), value -> settings.setAstFieldReference(Boolean.parseBoolean(value)));

        // Notify the listeners if the chat memory size has changed
        if (updateSettingIfModified(chatMemorySizeField, settings.getChatMemorySize(), value -> settings.setChatMemorySize(safeCastToInteger(value)))) {
            notifyChatMemorySizeChangeListeners();
        }
    }

    /**
     * Update the setting if the field value has changed
     * @param field        the field
     * @param currentValue the current value
     * @param updateAction the update action
     */
    public boolean updateSettingIfModified(JComponent field,
                                           Object currentValue,
                                           Consumer<String> updateAction) {
        String newValue = extractStringValue(field);
        if (newValue != null && !newValue.equals(currentValue)) {
            updateAction.accept(newValue);
            return true;
        }
        return false;
    }

    /**
     * Notify the chat memory size change listeners
     */
    public void notifyChatMemorySizeChangeListeners() {
        ApplicationManager.getApplication().getMessageBus()
            .syncPublisher(AppTopics.CHAT_MEMORY_SIZE_TOPIC)
            .onChatMemorySizeChanged(SettingsStateService.getInstance().getChatMemorySize());
    }

    /**
     * Extract the string value from the field
     * @param field the field
     * @return the string value
     */
    private @Nullable String extractStringValue(JComponent field) {
        if (field instanceof JTextField jtextfield) {
            return jtextfield.getText();
        } else if (field instanceof JCheckBox jcheckbox) {
            return Boolean.toString(jcheckbox.isSelected());
        }
        return null;
    }

    /**
     * Notify the listeners that the settings have changed
     */
    private void notifySettingsChanged() {
        MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
        messageBus.syncPublisher(AppTopics.SETTINGS_CHANGED_TOPIC).settingsChanged();
    }

    @Override
    public void reset() {
        SettingsStateService settingsState = SettingsStateService.getInstance();

        ollamaUrlField.setText(settingsState.getOllamaModelUrl());
        lmstudioUrlField.setText(settingsState.getLmstudioModelUrl());
        gpt4allUrlField.setText(settingsState.getGpt4allModelUrl());
        janUrlField.setText(settingsState.getJanModelUrl());

        chatMemorySizeField.setText(String.valueOf(settingsState.getChatMemorySize()));
        testPromptField.setText(settingsState.getTestPrompt());
        explainPromptField.setText(settingsState.getExplainPrompt());
        reviewPromptField.setText(settingsState.getReviewPrompt());
        customPromptField.setText(settingsState.getCustomPrompt());
        maxOutputTokensField.setText(settingsState.getMaxOutputTokens());

        streamModeCheckBox.setSelected(settingsState.getStreamMode());
        astReferenceFieldCheckBox.setSelected(settingsState.getAstFieldReference());
        astParentClassCheckBox.setSelected(settingsState.getAstParentClass());
        astReferenceClassesCheckBox.setSelected(settingsState.getAstClassReference());

        hideSearchCheckBox.setSelected(settingsState.getHideSearchButtonsFlag());

        setValue(temperatureField, settingsState.getTemperature());
        setValue(topPField, settingsState.getTopP());
        setValue(timeoutField, settingsState.getTimeout());
        setValue(retryField, settingsState.getMaxRetries());
        setValue(chatMemorySizeField, settingsState.getChatMemorySize());
    }

    /**
     * Set the value of the field
     * @param field the field
     * @param value the value
     */
    private static void setValue(JFormattedTextField field, Number value) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());

            if (value instanceof Double) {
                if (format instanceof DecimalFormat) {
                    ((DecimalFormat) format).applyPattern("#0.00");  // Ensures one decimal place in the display
                }
            } else {
                format.setParseIntegerOnly(true);
            }
            field.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(format)));
            field.setValue(value);
        } catch (IllegalArgumentException e) {
            // Handle the case where the string cannot be parsed to a number
            field.setValue(0);
        }
    }

    /**
     * Safely cast a string to an integer
     *
     * @param value the string value
     * @return the integer value
     */
    private @NotNull Integer safeCastToInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
