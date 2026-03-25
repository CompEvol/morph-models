open module morph.models {
    requires beast.base;
    requires beast.pkgmgmt;
    requires static beast.fx;
    requires static javafx.controls;

    exports morphmodels.evolution.substitutionmodel;
    exports morphmodels.evolution.alignment;
    exports morphmodels.app.beauti;

    provides beast.base.core.BEASTInterface with
        morphmodels.evolution.alignment.AscertainedForParsimonyUninformativeAlignment,
        morphmodels.evolution.alignment.AscertainedForParsimonyUninformativeFilteredAlignment,
        morphmodels.evolution.substitutionmodel.LewisMK,
        morphmodels.evolution.substitutionmodel.Ordinal,
        morphmodels.evolution.substitutionmodel.NestedOrdinal,
        morphmodels.app.beauti.BeautiMorphModelAlignmentProvider;
}
