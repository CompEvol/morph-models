open module beast.morph.models {
    requires beast.base;
    requires beast.pkgmgmt;

    exports morphmodels.evolution.substitutionmodel;
    exports morphmodels.evolution.alignment;

    provides beast.base.core.BEASTInterface with
        morphmodels.evolution.alignment.AscertainedForParsimonyUninformativeAlignment,
        morphmodels.evolution.alignment.AscertainedForParsimonyUninformativeFilteredAlignment,
        morphmodels.evolution.substitutionmodel.LewisMK,
        morphmodels.evolution.substitutionmodel.Ordinal,
        morphmodels.evolution.substitutionmodel.NestedOrdinal;
}
