package edu.wustl.cielo

class Constants {

    //For Paging
    public static final int DEFAULT_OFFSET          = 0
    public static final int DEFAULT_MAX             = 5

    //Comments
    public static final int DEFAULT_COMMENT_COUNT   = 2

    //For taglib - image div generation
    public static final String DEFAULT_IMG_SIZE     = 'medium'
    public static final int DEFAULT_STICKER_SIZE    = 52 // 48 + 4 px
    public static final Map IMAGE_SIZES             = ['xs': '-xs', 'small': '-sm', 'medium': '-md', 'large': '-lg',
                                                       'x-large': '-x-lg', 'xx-large': '-xx-lg', 'xxx-large': '-xxx-lg',
                                                       'huge': '-huge']

    //for bootstrap - creating projects in dev env
    public static final int TEAMS_PER_PROJECT           = 1
    public static final int CODES_PER_PROJECT           = 2
    public static final int DATAS_PER_PROJECT           = 3
    public static final int PUBLICATIONS_PER_PROJECT    = 1
    public static final int ANNOTATIONS_PER_PROJECT     = 2
    public static final int COMMENTS_PER_PROJECT        = 3

    //For GCS
    public static final String REPO_SUBDIR              = 'repos'
    public static final String REPO_MASTER              = 'master'
}
