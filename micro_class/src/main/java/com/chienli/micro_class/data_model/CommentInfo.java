package com.chienli.micro_class.data_model;

import java.io.Serializable;
import java.util.List;

public class CommentInfo implements Serializable{

    /**
     * pageNum : 1
     * pageSize : 10
     * size : 4
     * startRow : 1
     * endRow : 4
     * total : 4
     * pages : 1
     * list : [{"id":25,"userId":1,"discussType":"3","parentId":1,"discussText":"我的用户id为1,在id为1的视频中，发布的评论","discussTime":"2019-01-18 19:17:17.0","discussLikeSize":1,"answer":"0","hasNextPage":false,"replies":[],"userHead":null,"userName":null},
     *          {"id":24,"userId":1,"discussType":"3","parentId":1,"discussText":"我的用户id为1,在id为1的视频中，发布的评论","discussTime":"2019-01-17 19:17:15.0","discussLikeSize":1,"answer":"0","hasNextPage":false,"replies":[],"userHead":null,"userName":null},
     *          {"id":23,"userId":1,"discussType":"3","parentId":1,"discussText":"我的用户id为1,在id为1的视频中，发布的评论，被回复了，如果没有回复的数据，表示BUG了","discussTime":"2019-01-14 19:16:36.0","discussLikeSize":1,"answer":"1","hasNextPage":false,"replies":[{"id":1,"parentId":23,"sendName":"5号","receiveName":"1号","replyText":"5号用户回复1号用户的评论","replyTime":"2019-01-18 19:20:05.0"}],"userHead":null,"userName":null},
     *          {"id":4,"userId":2,"discussType":"3","parentId":1,"discussText":"我的用户id为2,在课堂视频的1号视频中,发表了评论(提问),被老师解答了","discussTime":"2018-12-19 16:22:24.0","discussLikeSize":32,"answer":"1","hasNextPage":false,"replies":[],"userHead":null,"userName":null}]
     * prePage : 0
     * nextPage : 0
     * isFirstPage : true
     * isLastPage : true
     * hasPreviousPage : false
     * hasNextPage : false
     * navigatePages : 8
     * navigatepageNums : [1]
     * navigateFirstPage : 1
     * navigateLastPage : 1
     * lastPage : 1
     * firstPage : 1
     */

    private int pageNum;
    private int pageSize;
    private int size;
    private int startRow;
    private int endRow;
    private int total;
    private int pages;
    private int prePage;
    private int nextPage;
    private boolean isFirstPage;
    private boolean isLastPage;
    private boolean hasPreviousPage;
    private boolean hasNextPage;
    private int navigatePages;
    private int navigateFirstPage;
    private int navigateLastPage;
    private int lastPage;
    private int firstPage;
    private List<CommentBean> list;
    private List<Integer> navigatepageNums;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public boolean isIsFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public boolean isIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int getNavigateFirstPage() {
        return navigateFirstPage;
    }

    public void setNavigateFirstPage(int navigateFirstPage) {
        this.navigateFirstPage = navigateFirstPage;
    }

    public int getNavigateLastPage() {
        return navigateLastPage;
    }

    public void setNavigateLastPage(int navigateLastPage) {
        this.navigateLastPage = navigateLastPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    public List<CommentBean> getList() {
        return list;
    }

    public void setList(List<CommentBean> list) {
        this.list = list;
    }

    public List<Integer> getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(List<Integer> navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }

    public static class CommentBean implements Serializable {
        /**
         * id : 23
         * userId : 1
         * discussType : 3
         * parentId : 1
         * discussText : 我的用户id为1,在id为1的视频中，发布的评论，被回复了，如果没有回复的数据，表示BUG了
         * discussTime : 2019-01-14 19:16:36.0
         * discussLikeSize : 1
         * answer : 1
         * hasNextPage : false
         * replies : [{"id":1,"parentId":23,"sendName":"5号","receiveName":"1号","replyText":"5号用户回复1号用户的评论","replyTime":"2019-01-18 19:20:05.0"}]
         * userHead : null
         * userName : null
         */

        private int id;
        private int userId;
        private String discussType;
        private int parentId;
        private String discussText;
        private String discussTime;
        private int discussLikeSize;
        private String answer;
        private boolean hasNextPage;
        private String userHead;
        private String userName;
        private List<RepliesBean> replies;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getDiscussType() {
            return discussType;
        }

        public void setDiscussType(String discussType) {
            this.discussType = discussType;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public String getDiscussText() {
            return discussText;
        }

        public void setDiscussText(String discussText) {
            this.discussText = discussText;
        }

        public String getDiscussTime() {
            return discussTime;
        }

        public void setDiscussTime(String discussTime) {
            this.discussTime = discussTime;
        }

        public int getDiscussLikeSize() {
            return discussLikeSize;
        }

        public void setDiscussLikeSize(int discussLikeSize) {
            this.discussLikeSize = discussLikeSize;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public boolean isHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }

        public String getUserHead() {
            return userHead;
        }

        public void setUserHead(String userHead) {
            this.userHead = userHead;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public List<RepliesBean> getReplies() {
            return replies;
        }

        public void setReplies(List<RepliesBean> replies) {
            this.replies = replies;
        }

        public static class RepliesBean implements Serializable {
            /**
             * id : 1
             * parentId : 23
             * sendName : 5号
             * receiveName : 1号
             * replyText : 5号用户回复1号用户的评论
             * replyTime : 2019-01-18 19:20:05.0
             */

            private int id;
            private int parentId;
            private String sendName;
            private String receiveName;
            private String replyText;
            private String replyTime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getParentId() {
                return parentId;
            }

            public void setParentId(int parentId) {
                this.parentId = parentId;
            }

            public String getSendName() {
                return sendName;
            }

            public void setSendName(String sendName) {
                this.sendName = sendName;
            }

            public String getReceiveName() {
                return receiveName;
            }

            public void setReceiveName(String receiveName) {
                this.receiveName = receiveName;
            }

            public String getReplyText() {
                return replyText;
            }

            public void setReplyText(String replyText) {
                this.replyText = replyText;
            }

            public String getReplyTime() {
                return replyTime;
            }

            public void setReplyTime(String replyTime) {
                this.replyTime = replyTime;
            }
        }
    }
}
