CREATE TABLE customers (
    id            BIGSERIAL       PRIMARY KEY,
    email         VARCHAR(255)    NOT NULL UNIQUE,
    name          VARCHAR(255)    NOT NULL,
    picture       TEXT,
    avatar        TEXT,
    created_at    TIMESTAMP       NOT NULL,
    last_login_at TIMESTAMP
);

CREATE TABLE campaigns (
    campaign_id          CHAR(16)        PRIMARY KEY,
    name                 VARCHAR(255),
    created_at           TIMESTAMPTZ,
    email_open_enabled   BOOLEAN         NOT NULL DEFAULT FALSE,
    time_tracker_enabled BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_email          VARCHAR(255)    NOT NULL,
    owner_name           VARCHAR(255)
);

CREATE TABLE witems (
    item_id     CHAR(16)    PRIMARY KEY,
    campaign_id CHAR(16)    NOT NULL,
    created_at  TIMESTAMPTZ,

    CONSTRAINT fk_witems_campaign
        FOREIGN KEY (campaign_id)
        REFERENCES campaigns (campaign_id)
        ON DELETE CASCADE
);

CREATE TABLE redirect_link_configs (
    id              BIGSERIAL       PRIMARY KEY,
    campaign_id     CHAR(16)        NOT NULL,
    label           VARCHAR(255),
    original_url    TEXT,
    password_hash   TEXT,
    view_once       BOOLEAN         NOT NULL DEFAULT FALSE,
    no_forwarding   BOOLEAN         NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_rlc_campaign
        FOREIGN KEY (campaign_id)
        REFERENCES campaigns (campaign_id)
        ON DELETE CASCADE
);

CREATE TABLE redirect_links (
    hash                VARCHAR(12)     PRIMARY KEY,
    item_id             CHAR(16)        NOT NULL,
    label               VARCHAR(255),
    original_url        TEXT,
    password_hash       TEXT,
    view_once           BOOLEAN         NOT NULL DEFAULT FALSE,
    view_once_consumed  BOOLEAN         NOT NULL DEFAULT FALSE,
    no_forwarding       BOOLEAN         NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_rl_item
        FOREIGN KEY (item_id)
        REFERENCES witems (item_id)
        ON DELETE CASCADE
);

CREATE TABLE open_events (
    id          BIGSERIAL       PRIMARY KEY,
    item_id     CHAR(16)        NOT NULL,
    timestamp   TIMESTAMPTZ,
    ip          VARCHAR(45),        -- supports IPv6
    user_agent  VARCHAR(512),

    CONSTRAINT fk_oe_item
        FOREIGN KEY (item_id)
        REFERENCES witems (item_id)
        ON DELETE CASCADE
);

CREATE TABLE click_events (
    id          BIGSERIAL       PRIMARY KEY,
    link_hash   VARCHAR(12)     NOT NULL,
    timestamp   TIMESTAMPTZ,
    ip          VARCHAR(45),
    user_agent  VARCHAR(512),

    CONSTRAINT fk_ce_link
        FOREIGN KEY (link_hash)
        REFERENCES redirect_links (hash)
        ON DELETE CASCADE
);

CREATE TABLE tracker_sessions (
    session_id       CHAR(40)    PRIMARY KEY,
    item_id          CHAR(16)    NOT NULL,
    started_at       TIMESTAMPTZ,
    duration_seconds BIGINT,         -- NULL until session ends
    ip               VARCHAR(45),

    CONSTRAINT fk_ts_item
        FOREIGN KEY (item_id)
        REFERENCES witems (item_id)
        ON DELETE CASCADE
);


CREATE INDEX idx_users_last_login ON customers (last_login_at);

CREATE INDEX idx_campaigns_owner_email ON campaigns (owner_email);
CREATE INDEX idx_campaigns_created_at  ON campaigns (created_at DESC);

CREATE INDEX idx_witems_campaign_id  ON witems (campaign_id);
CREATE INDEX idx_witems_created_at   ON witems (created_at DESC);

CREATE INDEX idx_rlc_campaign_id ON redirect_link_configs (campaign_id);

CREATE INDEX idx_rl_item_id ON redirect_links (item_id);

-- ----- open_events -----
-- Time-range queries per item (e.g. "opens in the last 7 days").
CREATE INDEX idx_oe_item_id   ON open_events (item_id);
CREATE INDEX idx_oe_timestamp ON open_events (timestamp DESC);
-- Composite: item + time — the most common analytics query.
CREATE INDEX idx_oe_item_timestamp ON open_events (item_id, timestamp DESC);

-- ----- click_events -----
-- Clicks per link.
CREATE INDEX idx_ce_link_hash  ON click_events (link_hash);
CREATE INDEX idx_ce_timestamp  ON click_events (timestamp DESC);
-- Composite: link + time.
CREATE INDEX idx_ce_link_timestamp ON click_events (link_hash, timestamp DESC);

-- ----- tracker_sessions -----
CREATE INDEX idx_ts_item_id    ON tracker_sessions (item_id);
CREATE INDEX idx_ts_started_at ON tracker_sessions (started_at DESC);
