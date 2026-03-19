open module beast.morph.models.fx {
    requires beast.morph.models;
    requires beast.base;
    requires beast.pkgmgmt;
    requires beast.fx;
    requires javafx.controls;

    exports morphmodels.app.beauti;

    provides beast.base.core.BEASTInterface with
        morphmodels.app.beauti.BeautiMorphModelAlignmentProvider;
}
