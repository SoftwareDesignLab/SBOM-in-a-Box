package org.svip.sbomfactory.translators;

public abstract class TranslatorTestCore<T extends TranslatorCore> {
    protected T TRANSLATOR;

    protected TranslatorTestCore(T translator) {
        setDummyTranslator(translator);
    }

    private void setDummyTranslator(T translator) {
        this.TRANSLATOR = translator;
    }
}
