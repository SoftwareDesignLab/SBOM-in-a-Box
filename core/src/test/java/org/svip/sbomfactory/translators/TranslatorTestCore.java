package org.svip.sbomfactory.translators;

public abstract class TranslatorTestCore<T extends TranslatorCore> {
    protected T TRANSLATOR;

    // TODO: Docstring
    protected TranslatorTestCore(T translator) {
        setDummyTranslator(translator);
    }

    // TODO: Docstring
    private void setDummyTranslator(T translator) {
        // TODO: Any needed dummy setup
//        final Path path = Paths.get(src);
//        translator.setPWD(path);
//        translator.setSRC(path);
        this.TRANSLATOR = translator;
    }
}
