package com.echologic.data.local

/**
 * Pre-seeds the Room database with curated funny, absurd, and "dumb logic" quotes.
 * Categories map to the "Vibe" system in the Library screen.
 */
object QuoteSeeder {

    fun getInitialQuotes(): List<QuoteEntity> = listOf(
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── LOGIC LOOPS ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("ll-01", "If you clean a vacuum cleaner, you become the vacuum cleaner.", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-02", "If your shirt isn't tucked into your pants, then your pants are tucked into your shirt.", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-03", "Every time you stand up, you defy the entire planet.", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-04", "You've never been in an empty room.", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-05", "The word 'incorrectly' is always spelled incorrectly except when it's spelled 'incorrectly'.", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-06", "If two mind readers read each other's minds, whose mind are they reading?", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-07", "You can't stand backwards on stairs.", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-08", "Every mirror you buy is used.", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-09", "The youngest picture of you is also the oldest picture of you.", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-10", "When you say 'forward' your lips move forward. When you say 'back' your lips move back.", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-11", "If you wait for a waiter, does that make you the waiter?", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-12", "You're not stuck in traffic. You ARE traffic.", "Logic Loops", "Anonymous"),
        QuoteEntity("ll-13", "If nothing sticks to Teflon, how do they make Teflon stick to the pan?", "Logic Loops", "Steven Wright"),
        QuoteEntity("ll-14", "I used to think the brain was the most important organ. Then I thought, look what's telling me that.", "Logic Loops", "Emo Philips"),
        QuoteEntity("ll-15", "If a word is misspelled in the dictionary, how would we ever know?", "Logic Loops", "Steven Wright"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── SHOWER THOUGHTS ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("st-01", "Your stomach thinks all potatoes are mashed.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-02", "Aliens probably ride past Earth and lock their doors.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-03", "History classes will only get longer and harder over time.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-04", "Nothing is on fire. Fire is on things.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-05", "Your future self is watching you through memories.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-06", "When you drink water you're just watering yourself.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-07", "The letter W starts with a D.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-08", "Teeth are the only bones you clean.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-09", "You never realize how long a minute is until you're doing a plank.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-10", "A sunset is actually a sunrise somewhere else.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-11", "Grocery stores are just warehouses that charge you to take stuff out.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-12", "Your age is just the number of laps you've done around a giant fireball.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-13", "Sleeping is just free trial death.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-14", "Technically, every book is a remix of the dictionary.", "Shower Thoughts", "Anonymous"),
        QuoteEntity("st-15", "We drive on parkways and park on driveways.", "Shower Thoughts", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── ABSURD ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("ab-01", "I put my grandma on speed dial. I call that Instagram.", "Absurd", "Anonymous"),
        QuoteEntity("ab-02", "I told my wifi we need to connect. It said the connection was unstable.", "Absurd", "Anonymous"),
        QuoteEntity("ab-03", "My bed is a magical place where I suddenly remember everything I forgot to do.", "Absurd", "Anonymous"),
        QuoteEntity("ab-04", "I'm on a seafood diet. I see food and I eat it.", "Absurd", "Anonymous"),
        QuoteEntity("ab-05", "I don't need a hair stylist. My pillow gives me a new hairstyle every morning.", "Absurd", "Anonymous"),
        QuoteEntity("ab-06", "I finally found my spirit animal. It's a sloth.", "Absurd", "Anonymous"),
        QuoteEntity("ab-07", "My therapist says I have a preoccupation with vengeance. We'll see about that.", "Absurd", "Stewart Francis"),
        QuoteEntity("ab-08", "I used to think I was indecisive. But now I'm not so sure.", "Absurd", "Anonymous"),
        QuoteEntity("ab-09", "I'm not lazy. I'm just on energy-saving mode.", "Absurd", "Anonymous"),
        QuoteEntity("ab-10", "I told my suitcase we're not going on vacation. Now I'm dealing with emotional baggage.", "Absurd", "Anonymous"),
        QuoteEntity("ab-11", "Light travels faster than sound. That's why some people appear bright until you hear them speak.", "Absurd", "Alan Dundes"),
        QuoteEntity("ab-12", "I am so clever that sometimes I don't understand a single word of what I am saying.", "Absurd", "Oscar Wilde"),
        QuoteEntity("ab-13", "I refuse to answer that question on the grounds that I don't know the answer.", "Absurd", "Douglas Adams"),
        QuoteEntity("ab-14", "The difference between stupidity and genius is that genius has its limits.", "Absurd", "Albert Einstein"),
        QuoteEntity("ab-15", "I have not failed. I've just found 10,000 ways that won't work.", "Absurd", "Thomas Edison"),
        QuoteEntity("ab-16", "People say nothing is impossible, but I do nothing every day.", "Absurd", "A.A. Milne"),
        QuoteEntity("ab-17", "I can resist everything except temptation.", "Absurd", "Oscar Wilde"),
        QuoteEntity("ab-18", "Always borrow money from a pessimist. They'll never expect it back.", "Absurd", "Oscar Wilde"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── DEEP DUMB ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("dd-01", "If you try to fail and succeed, which have you done?", "Deep Dumb", "George Carlin"),
        QuoteEntity("dd-02", "If you enjoy wasting time, is it really wasted?", "Deep Dumb", "John Lennon"),
        QuoteEntity("dd-03", "Are eyebrows facial hair?", "Deep Dumb", "Anonymous"),
        QuoteEntity("dd-04", "If you hit yourself and it hurts, are you strong or weak?", "Deep Dumb", "Anonymous"),
        QuoteEntity("dd-05", "Why is the pizza box a square, the pizza a circle, and the slice a triangle?", "Deep Dumb", "Anonymous"),
        QuoteEntity("dd-06", "If life is unfair to everyone, doesn't that make life fair?", "Deep Dumb", "Anonymous"),
        QuoteEntity("dd-07", "Are oranges named orange because they're orange, or is orange named orange because of oranges?", "Deep Dumb", "Anonymous"),
        QuoteEntity("dd-08", "If you drop soap on the floor, is the floor clean or is the soap dirty?", "Deep Dumb", "Anonymous"),
        QuoteEntity("dd-09", "Why do we press harder on the remote when the battery is low?", "Deep Dumb", "Anonymous"),
        QuoteEntity("dd-10", "If Cinderella's shoe fit perfectly, why did it fall off?", "Deep Dumb", "Anonymous"),
        QuoteEntity("dd-11", "Why do they call it a building if it's already built?", "Deep Dumb", "Anonymous"),
        QuoteEntity("dd-12", "If you're trying to be normal, does that make you abnormal?", "Deep Dumb", "Anonymous"),
        QuoteEntity("dd-13", "Why does mineral water that has trickled through mountains for centuries have a use-by date?", "Deep Dumb", "Peter Kay"),
        QuoteEntity("dd-14", "If man evolved from monkeys, why do we still have monkeys?", "Deep Dumb", "Anonymous"),
        QuoteEntity("dd-15", "When cheese gets its picture taken, what does it say?", "Deep Dumb", "George Carlin"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── ACCIDENTAL PHILOSOPHY ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("ap-01", "The brain named itself.", "Accidental Philosophy", "Anonymous"),
        QuoteEntity("ap-02", "Every person you've ever dreamed about, your brain invented their face.", "Accidental Philosophy", "Anonymous"),
        QuoteEntity("ap-03", "You will never know if you successfully did nothing.", "Accidental Philosophy", "Anonymous"),
        QuoteEntity("ap-04", "We cook bacon and bake cookies.", "Accidental Philosophy", "Anonymous"),
        QuoteEntity("ap-05", "If you replace every part of a ship, is it still the same ship?", "Accidental Philosophy", "Plutarch"),
        QuoteEntity("ap-06", "The object of golf is to play the least amount of golf.", "Accidental Philosophy", "Anonymous"),
        QuoteEntity("ap-07", "We have never seen our own face, only reflections and photographs.", "Accidental Philosophy", "Anonymous"),
        QuoteEntity("ap-08", "At some point, your parents put you down and never picked you up again.", "Accidental Philosophy", "Anonymous"),
        QuoteEntity("ap-09", "Maybe plants are farming us by giving us oxygen until we decompose and return to the soil.", "Accidental Philosophy", "Anonymous"),
        QuoteEntity("ap-10", "The real question is: did the chicken cross the road, or did the road cross under the chicken?", "Accidental Philosophy", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── BRAINROT ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("br-01", "What the sigma?", "Brainrot", "Anonymous"),
        QuoteEntity("br-02", "Level 100 Mafia Boss energy.", "Brainrot", "Anonymous"),
        QuoteEntity("br-03", "Looksmaxxing until I ascend.", "Brainrot", "Anonymous"),
        QuoteEntity("br-04", "Is it rizz or is it a restraining order?", "Brainrot", "Anonymous"),
        QuoteEntity("br-05", "Skibidi bop bop yes yes.", "Brainrot", "Anonymous"),
        QuoteEntity("br-06", "Sigma grindset: wake up, ignore everyone.", "Brainrot", "Anonymous"),
        QuoteEntity("br-07", "Only true gigachads understand.", "Brainrot", "Anonymous"),
        QuoteEntity("br-08", "You're getting mogged just looking at this.", "Brainrot", "Anonymous"),
        QuoteEntity("br-09", "Absolute cinema.", "Brainrot", "Anonymous"),
        QuoteEntity("br-10", "Bro let the intrusive thoughts win.", "Brainrot", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── CELEBRITY DUMB ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("cd-01", "I'm not superstitious, but I am a little stitious.", "Celebrity Dumb", "Michael Scott"),
        QuoteEntity("cd-02", "Sometimes I'll start a sentence and I don't even know where it's going.", "Celebrity Dumb", "Michael Scott"),
        QuoteEntity("cd-03", "I know that I am intelligent, because I know that I know nothing.", "Celebrity Dumb", "Socrates"),
        QuoteEntity("cd-04", "I generally avoid temptation unless I can't resist it.", "Celebrity Dumb", "Mae West"),
        QuoteEntity("cd-05", "I cook with wine. Sometimes I even add it to the food.", "Celebrity Dumb", "W.C. Fields"),
        QuoteEntity("cd-06", "All generalizations are false, including this one.", "Celebrity Dumb", "Mark Twain"),
        QuoteEntity("cd-07", "A clear conscience is a sure sign of a bad memory.", "Celebrity Dumb", "Mark Twain"),
        QuoteEntity("cd-08", "Get your facts first, then you can distort them as you please.", "Celebrity Dumb", "Mark Twain"),
        QuoteEntity("cd-09", "Don't cry because it's over, smile because it happened.", "Celebrity Dumb", "Dr. Seuss"),
        QuoteEntity("cd-10", "Two things are infinite: the universe and human stupidity; and I'm not sure about the universe.", "Celebrity Dumb", "Albert Einstein"),
        QuoteEntity("cd-11", "The secret of life is honesty and fair dealing. If you can fake that, you've got it made.", "Celebrity Dumb", "Groucho Marx"),
        QuoteEntity("cd-12", "Outside of a dog, a book is man's best friend. Inside of a dog, it's too dark to read.", "Celebrity Dumb", "Groucho Marx"),
        QuoteEntity("cd-13", "I find television very educational. Every time someone turns it on, I go read a book.", "Celebrity Dumb", "Groucho Marx"),
        QuoteEntity("cd-14", "Go to Heaven for the climate, Hell for the company.", "Celebrity Dumb", "Mark Twain"),
        QuoteEntity("cd-15", "Do not take life too seriously. You will never get out of it alive.", "Celebrity Dumb", "Elbert Hubbard"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── AURA & PRESENCE ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("br_aura_001", "If your aura ain’t glowing, you probably forgot to update your rizz firmware.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_002", "Never chase vibes—be the update that installs them.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_003", "Your aura got nerfed because you forgot to believe in the patch notes.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_004", "Your glow-up is buffering—be patient.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_005", "Aura doesn’t shout—it just silently mogs.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_006", "Your vibe attracts your tribe… unless your vibe is lagging.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_007", "Your aura expands when you stop explaining yourself.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_008", "Your peace is worth more than their attention.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_009", "Goated energy doesn’t need announcements.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_010", "Your aura doesn’t need WiFi—it’s always connected.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_011", "Aura farming requires consistency, not approval.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_012", "Even silence can be loud if your presence is strong.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_013", "Your aura speaks before you do.", "Aura & Vibe", "Anonymous"),
        QuoteEntity("br_aura_014", "Aura grows in silence, not noise.", "Aura & Vibe", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── MINDSET GROWTH ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("br_mindset_001", "A goated mindset turns Ls into limited edition collectibles.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_002", "You didn’t fail, you just got early access to character development.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_003", "Every L is just a prequel to your comeback arc.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_004", "Don’t crash out over pixels in a temporary simulation.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_005", "The real flex is peace, not clout.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_006", "Goated minds stay unbothered in chaotic servers.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_007", "A calm mind hits different in loud situations.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_008", "Every situation is either lore or lesson.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_009", "You can’t fix everything, but you can vibe through it.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_010", "A real one adapts, not complains.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_011", "Goated behavior is silent consistency.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_012", "Your glow-up scares people stuck in beta mode.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_013", "Goated minds don’t compete—they create.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_014", "Goated people move differently, not loudly.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_015", "Goated energy is built, not borrowed.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_016", "You’re not behind—you’re just loading.", "Mindset Growth", "Anonymous"),
        QuoteEntity("br_mindset_017", "Every ending is a disguised beginning.", "Mindset Growth", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── SKIBIDI CHAOS ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("br_skibidi_001", "Skibidi wisdom: sometimes you gotta flush the vibe before you upgrade it.", "Skibidi Chaos", "Anonymous"),
        QuoteEntity("br_skibidi_002", "Skibidi energy flows where overthinking logs out.", "Skibidi Chaos", "Anonymous"),
        QuoteEntity("br_skibidi_003", "Skibidi logic: chaos is just misunderstood rhythm.", "Skibidi Chaos", "Anonymous"),
        QuoteEntity("br_skibidi_004", "Skibidi truth: not everything needs a reason, just a rhythm.", "Skibidi Chaos", "Anonymous"),
        QuoteEntity("br_skibidi_005", "Skibidi patience unlocks secret endings.", "Skibidi Chaos", "Anonymous"),
        QuoteEntity("br_skibidi_006", "Skibidi insight: weird is just unoptimized genius.", "Skibidi Chaos", "Anonymous"),
        QuoteEntity("br_skibidi_007", "Even chaos has a pattern if you stare long enough.", "Skibidi Chaos", "Anonymous"),
        QuoteEntity("br_skibidi_008", "Skibidi truth: nothing matters, so make it matter.", "Skibidi Chaos", "Anonymous"),
        QuoteEntity("br_skibidi_009", "You are both the glitch and the fix.", "Skibidi Chaos", "Anonymous"),
        QuoteEntity("br_skibidi_010", "Skibidi balance is chaos and calm coexisting.", "Skibidi Chaos", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── LET HIM COOK ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("br_cook_001", "Let him cook… even if the kitchen is imaginary.", "Let Him Cook", "Anonymous"),
        QuoteEntity("br_cook_002", "Let intrusive thoughts cook—they might drop a banger.", "Let Him Cook", "Anonymous"),
        QuoteEntity("br_cook_003", "Let him cook, even if he’s burning imaginary toast.", "Let Him Cook", "Anonymous"),
        QuoteEntity("br_cook_004", "Let him cook, even if it’s just vibes.", "Let Him Cook", "Anonymous"),
        QuoteEntity("br_cook_005", "Let him cook—results speak louder than explanations.", "Let Him Cook", "Anonymous"),
        QuoteEntity("br_cook_006", "Let him cook—even failure is seasoning.", "Let Him Cook", "Anonymous"),
        QuoteEntity("br_cook_007", "Let him cook—trust the chaos.", "Let Him Cook", "Anonymous"),
        QuoteEntity("br_cook_008", "Let him cook—greatness needs time.", "Let Him Cook", "Anonymous"),
        QuoteEntity("br_cook_009", "Let him cook—trust your process.", "Let Him Cook", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── NPC VS MAIN CHARACTER ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("br_npc_001", "Your haters are just NPCs stuck in side quests.", "NPC vs Main Character", "Anonymous"),
        QuoteEntity("br_npc_002", "If you’re lost, follow the WiFi of your intuition.", "NPC vs Main Character", "Anonymous"),
        QuoteEntity("br_npc_003", "Even NPCs think they’re the main character.", "NPC vs Main Character", "Anonymous"),
        QuoteEntity("br_npc_004", "Don’t argue with NPCs—they don’t drop loot.", "NPC vs Main Character", "Anonymous"),
        QuoteEntity("br_npc_005", "You are both the glitch and the fix.", "NPC vs Main Character", "Anonymous"),
        QuoteEntity("br_npc_006", "In a world of NPCs, dare to be the main quest.", "NPC vs Main Character", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── RIZZ & SOCIAL ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("br_rizz_001", "Your rizz is not low, your audience is just bugged.", "Rizz & Social", "Anonymous"),
        QuoteEntity("br_rizz_002", "He who yaps less, rizzes more.", "Rizz & Social", "Anonymous"),
        QuoteEntity("br_rizz_003", "He who holds the rizz controls the timeline.", "Rizz & Social", "Anonymous"),
        QuoteEntity("br_rizz_004", "Your vibe attracts your tribe… unless your vibe is lagging.", "Rizz & Social", "Anonymous"),
        QuoteEntity("br_rizz_005", "Your rizz increases when you stop trying to rizz.", "Rizz & Social", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── FANUM TAX ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("br_fanum_001", "He who fears the Fanum tax will never taste the full buffet of life.", "Fanum Tax", "Anonymous"),
        QuoteEntity("br_fanum_002", "Fanum tax is temporary, but drip is eternal.", "Fanum Tax", "Anonymous"),
        QuoteEntity("br_fanum_003", "Fanum tax applies even to emotional snacks.", "Fanum Tax", "Anonymous"),
        QuoteEntity("br_fanum_004", "Fanum tax is just the universe reminding you to share snacks.", "Fanum Tax", "Anonymous"),
        QuoteEntity("br_fanum_005", "Fanum tax of life: you pay in time, not money.", "Fanum Tax", "Anonymous"),
        QuoteEntity("br_fanum_006", "Fanum tax applies to energy too—spend wisely.", "Fanum Tax", "Anonymous"),
        QuoteEntity("br_fanum_007", "Fanum tax: the cost of existing is snacks.", "Fanum Tax", "Anonymous"),
        QuoteEntity("br_fanum_008", "Fanum tax hits harder when you’re hungry for validation.", "Fanum Tax", "Anonymous"),
        QuoteEntity("br_fanum_009", "Fanum tax reminds you nothing is truly free.", "Fanum Tax", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── CRASH OUT ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("br_crash_001", "Crash out today so tomorrow you can respawn with better stats.", "Crash Out", "Anonymous"),
        QuoteEntity("br_crash_002", "A true king crashes out privately and posts highlights publicly.", "Crash Out", "Anonymous"),
        QuoteEntity("br_crash_003", "Crash out less, lock in more.", "Crash Out", "Anonymous"),
        QuoteEntity("br_crash_004", "Stay mysterious—confusion is a form of aura.", "Crash Out", "Anonymous"),
        QuoteEntity("br_crash_005", "Crash out less, meditate more.", "Crash Out", "Anonymous"),
        QuoteEntity("br_crash_006", "Crash out in private, glow up in public.", "Crash Out", "Anonymous"),
        QuoteEntity("br_crash_007", "Crash out once, reflect twice.", "Crash Out", "Anonymous"),
        QuoteEntity("br_crash_008", "Crash out less, evolve more.", "Crash Out", "Anonymous"),
        QuoteEntity("br_crash_009", "Crash out or level up—choose wisely.", "Crash Out", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── PEACE & DETACHMENT ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("br_peace_001", "Wisdom is knowing when to log off arguments.", "Peace & Detachment", "Anonymous"),
        QuoteEntity("br_peace_002", "If it feels illegal but it’s not, it’s probably growth.", "Peace & Detachment", "Anonymous"),
        QuoteEntity("br_peace_003", "If they misunderstand you, upgrade your silence.", "Peace & Detachment", "Anonymous"),
        QuoteEntity("br_peace_004", "Aura grows when ego logs out.", "Peace & Detachment", "Anonymous"),
        QuoteEntity("br_peace_005", "If it drains you, uninstall it.", "Peace & Detachment", "Anonymous"),
        QuoteEntity("br_peace_006", "If it feels wrong, your inner algorithm is warning you.", "Peace & Detachment", "Anonymous"),
        QuoteEntity("br_peace_007", "Your vibe is your strongest currency.", "Peace & Detachment", "Anonymous"),
        QuoteEntity("br_peace_008", "If they don’t get it, it wasn’t meant for them.", "Peace & Detachment", "Anonymous"),
        QuoteEntity("br_peace_009", "Your peace is your ultimate flex.", "Peace & Detachment", "Anonymous"),

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // ── LIFE SIMULATION ──
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        QuoteEntity("br_life_001", "If reality glitches, just act like it’s part of your lore.", "Life Simulation", "Anonymous"),
        QuoteEntity("br_life_002", "Sometimes the best strategy is to disconnect and reconnect your brain.", "Life Simulation", "Anonymous"),
        QuoteEntity("br_life_003", "You can’t speedrun healing, but you can skip toxic DLC.", "Life Simulation", "Anonymous"),
        QuoteEntity("br_life_004", "If you’re lost, follow the WiFi of your intuition.", "Life Simulation", "Anonymous"),
        QuoteEntity("br_life_005", "Don’t crash out over pixels in a temporary simulation.", "Life Simulation", "Anonymous"),
        QuoteEntity("br_life_006", "Your mind is a server—don’t let randoms join.", "Life Simulation", "Anonymous"),
        QuoteEntity("br_life_007", "If it feels wrong, your inner algorithm is warning you.", "Life Simulation", "Anonymous"),
        QuoteEntity("br_life_008", "Don’t overthink—overthinking is a scam DLC.", "Life Simulation", "Anonymous"),
    )
}
