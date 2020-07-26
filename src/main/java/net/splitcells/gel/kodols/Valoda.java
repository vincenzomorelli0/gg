package net.splitcells.gel.kodols;

public enum Valoda {
    PIEŠĶIRT("piešķirt")
    ,CEĻA_PIEEJAS_SIMBOLS(".")
    ,PRASĪBA("prasība")
    ,PIEDĀVĀJUMS("piedāvājums")
    , NOŅEMT("noņemt")
    , PĀRBAUDE("pārbaude")
    , RINDA("rinda")
    , PIEDĀVĀJUMI("piedāvājumi")
    , PRASĪBAS("parsības")
    , VĒSTURE("vēsture")
    , PIEŠĶIRŠANA("piešķiršana")
    , PIEŠĶIRŠANAS("piešķiršanas")
    , REFKLEKSIJAS_DATI("refkleksijasDati")
    , DAIT("dati")
    , VERTĪBA("vertība")
    , ATSLĒGA("atslēga")
    , TIPS("tips")
    , PARSĪBA("parsība")
    , SOLU_TIPS("soluTips")
    ;
    private final String apraksts;

    Valoda(String apraksts) {
        this.apraksts = apraksts;
    }

    public String apraksts() {
        return apraksts;
    }
}
