INSERT INTO charities (name, description, image_url, featured)
SELECT 'First Tee Youth Foundation',
       'Supports youth golf education, mentorship, and life-skills development.',
       'https://images.unsplash.com/photo-1471295253337-3ceaaedca402?auto=format&fit=crop&w=1200&q=80',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM charities WHERE name = 'First Tee Youth Foundation'
);

INSERT INTO charities (name, description, image_url, featured)
SELECT 'Green Fairways Environment Trust',
       'Funds sustainable water use, turf recovery, and eco-friendly course programs.',
       'https://images.unsplash.com/photo-1465101046530-73398c7f28ca?auto=format&fit=crop&w=1200&q=80',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM charities WHERE name = 'Green Fairways Environment Trust'
);

INSERT INTO charities (name, description, image_url, featured)
SELECT 'Women in Golf Initiative',
       'Creates coaching, tournament access, and scholarship opportunities for women golfers.',
       'https://images.unsplash.com/photo-1518600506278-4e8ef466b810?auto=format&fit=crop&w=1200&q=80',
       FALSE
WHERE NOT EXISTS (
    SELECT 1 FROM charities WHERE name = 'Women in Golf Initiative'
);

INSERT INTO charities (name, description, image_url, featured)
SELECT 'Adaptive Golf Access Program',
       'Expands adaptive equipment and coaching for players with disabilities.',
       'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1200&q=80',
       FALSE
WHERE NOT EXISTS (
    SELECT 1 FROM charities WHERE name = 'Adaptive Golf Access Program'
);
