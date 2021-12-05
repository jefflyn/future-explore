create table future_basic
(
    symbol varchar(4) not null comment '商品代号'
        primary key,
    name varchar(16) not null comment '商品名',
    type varchar(8) not null comment '分类',
    code varchar(8) not null comment '合约代码',
    low decimal(10,2) not null comment '主连历史最低',
    high decimal(10,2) not null comment '主连历史最高',
    a decimal(10,2) null comment '本合约A（合约变更需修改）',
    b decimal(10,2) null comment '本合约B（合约变更需修改）',
    c decimal(10,2) null comment '本合约C（合约变更需修改）',
    amount int not null comment '合同每单位数量',
    unit varchar(4) not null comment '单位',
    night tinyint not null comment '是否夜盘（0=否 1=是）',
    exchange varchar(16) not null comment '所属交易所',
    is_target tinyint default 1 not null comment '0=否 1=是',
    deleted tinyint default 0 not null,
    remark varchar(64) null comment 'remark',
    update_time timestamp not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '合约基本信息';

create table future_collect
(
    id bigint auto_increment comment '主键id'
        primary key,
    trade_date varchar(10) not null comment '交易日期',
    code varchar(10) not null comment '合约代码',
    name varchar(20) not null comment '合约名称',
    type tinyint(1) not null comment '采集类型',
    price decimal(10,2) not null comment '现价',
    position tinyint default 0 not null comment '相对位置',
    high decimal(10,2) not null comment '最高价',
    low decimal(10,2) not null comment '最低价',
    deal_vol int null comment '交易量',
    hold_vol int null comment '持仓量',
    create_time timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    remark varchar(64) null
)
    comment '定时数据采集分析';

create index idx_future_collect_trade_date
    on future_collect (trade_date);

create index idx_future_daily_collect_code_date
    on future_collect (code, trade_date);

create table future_daily
(
    id bigint auto_increment comment '主键id'
        primary key,
    symbol varchar(6) not null comment '商品代码',
    trade_date varchar(10) not null comment '交易日期',
    code varchar(10) null comment '合约代码',
    name varchar(20) not null comment '合约名称',
    close decimal(10,2) null comment '收盘价',
    close_change decimal(10,2) null comment '昨收盘价涨跌幅',
    settle decimal(10,2) null comment '结算价',
    settle_change decimal(10,2) null comment '结算价涨跌幅',
    open decimal(10,2) null comment '开盘价',
    high decimal(10,2) null comment '最高价',
    low decimal(10,2) null comment '最低价',
    hl_diff decimal(10,2) null comment '高低价差',
    amplitude decimal(10,2) null comment '振幅',
    pre_close decimal(10,2) null comment '昨收盘价',
    pre_settle decimal(10,2) null comment '昨结算价',
    deal_vol int null comment '交易量',
    hold_vol int null comment '持仓量',
    exchange varchar(10) null comment '交易所',
    create_time datetime not null comment '创建时间',
    update_time datetime not null comment '更新时间',
    remark varchar(64) null,
    constraint uidx_future_daily_trade_date_contract_code
        unique (trade_date, code)
)
    comment '每日数据';

create index idx_future_daily_code
    on future_daily (symbol);

create index idx_future_daily_code_date
    on future_daily (code, trade_date);

create table future_live
(
    code varchar(8) not null comment '合约code'
        primary key,
    type varchar(8) not null comment '品种',
    name varchar(16) not null comment '合约名',
    price decimal(10,2) not null comment '现价',
    `change` decimal(10,2) not null comment '涨跌幅%',
    position tinyint default 0 not null comment '相对位置%',
    bid1 decimal(10,2) null comment 'buy 1',
    ask1 decimal(10,2) null comment 'sell 1',
    open decimal(10,2) null comment '开',
    low decimal(10,2) null comment '最低',
    high decimal(10,2) null comment '最高',
    amp decimal(10,2) null comment '振幅%',
    lowest_change decimal(10,2) null comment '历史最低涨跌幅%',
    highest_change decimal(10,2) null comment '历史最高涨跌幅%',
    wave varchar(64) null comment '波动',
    update_time datetime not null comment '最新更新时间'
)
    comment '实时';

create table future_log
(
    id bigint auto_increment comment 'id'
        primary key,
    trade_date varchar(10) not null comment 'trade date',
    code varchar(8) not null comment 'contract code',
    name varchar(10) not null comment '合约名称',
    type varchar(10) null comment '类型',
    factor int null comment '监控因子',
    diff decimal(10,2) null comment '监控值',
    content varchar(64) null comment '内容',
    `option` varchar(6) not null comment '操作选择',
    suggest decimal(10,2) not null comment '建议价格',
    pct_change decimal(10,2) null comment '当前涨跌幅',
    position int null comment '相对位置',
    log_time timestamp default CURRENT_TIMESTAMP not null comment '时间',
    remark varchar(32) null comment '备注'
)
    comment '日志';

create index idx_future_log_code_date
    on future_log (code, trade_date);

create index idx_future_log_trade_date
    on future_log (trade_date);

create table open_gap
(
    trade_date varchar(10) not null comment 'trade date',
    code varchar(8) not null,
    name varchar(10) not null comment 'name',
    category varchar(8) default '' not null comment 'category',
    pre_close decimal(10,2) not null comment 'pre close',
    pre_high decimal(10,2) null comment 'pre high',
    pre_low decimal(10,2) null comment 'pre low',
    open decimal(10,2) not null comment 'current open',
    gap_rate decimal(10,2) not null comment 'gap rate',
    day_gap tinyint null comment 'is day gap',
    remark varchar(64) null comment 'remark',
    buy_low decimal(10,2) not null comment 'low price for buy',
    sell_high decimal(10,2) not null comment 'high price for sell',
    create_time timestamp default CURRENT_TIMESTAMP not null,
    constraint uidx_open_gap_code_trade_date
        unique (code, trade_date)
)
    comment 'open gap log';

create index idx_open_gap_trade_date
    on open_gap (trade_date);
