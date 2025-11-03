# alter table categories
#     add column is_deleted boolean default false not null,
#     add column is_visible boolean default true not null;

# alter table display_categories
#     add column media_type enum('IMAGE', 'VIDEO') not null;

# alter table display_categories
#     add column `order` int default 0;

# alter table display_categories
#     add column `position` enum('HOMEPAGE', 'SIDEBAR') not null;

# alter table display_categories
#     change `order` `display_order` INT NOT NULL DEFAULT 0;