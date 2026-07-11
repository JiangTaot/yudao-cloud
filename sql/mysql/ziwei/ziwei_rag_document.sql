-- =====================================================
-- 紫微斗数 RAG 知识库 - 文档表
-- =====================================================

DROP TABLE IF EXISTS ziwei_rag_document;
CREATE TABLE ziwei_rag_document (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    book_title VARCHAR(200) NOT NULL COMMENT '书名',
    book_author VARCHAR(100) COMMENT '作者/来源',
    file_name VARCHAR(300) NOT NULL COMMENT '原始文件名',
    file_url VARCHAR(500) NOT NULL COMMENT 'MinIO 文件 URL',
    file_size BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小（bytes）',
    segment_count INT DEFAULT 0 COMMENT '切分段数（向量化完成后更新）',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-处理中 1-已完成 2-失败',
    error_message VARCHAR(1000) COMMENT '失败原因（status=2 时记录）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creator VARCHAR(64), updater VARCHAR(64), deleted BIT DEFAULT 0,
    INDEX idx_book_title (book_title),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='紫微斗数-RAG 古籍文档';
