package com.performetriks.gatlytron.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

/**************************************************************************************************************
 * Utility Class copied and adjusted from the CoreFramework project (Class: com.xresch.cfw.utils.CFWRandom).
 * 
 * Copyright Owner: Performetriks GmbH, Switzerland
 * License: MIT License
 * 
 * @author Reto Scheiwiller
 * 
 **************************************************************************************************************/
public class GatlytronRandom {

	private static final GatlytronRandom INSTANCE = new GatlytronRandom();
	
	private static final Random random = new Random();
	
	public static final String ALPHAS 	  = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String ALPHA_NUMS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345678901234567890";
	public static final String ALPHA_NUMS_SPECIALS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345678901234567890+*%&/()=?!{}[]><:;.,-_+*%&/()=?!{}[]><:;.,-_";
		
	private static final String[] firstnameGods = new String[] {"Zeus", "Hera", "Poseidon", "Cronus", "Aphrodite", "Hades", "Hephaestus", "Apollo", "Athena", "Artemis", "Ares", "Hermes", "Dionysus", "Persephone", "Eros", "Gaia", "Hypnos", "Rhea", "Uranus", "Nike", "Eos", "Pan", "Selene", "Helios", "Heracles", "Odysseus", "Jupiter", "Juno", "Neptune", "Saturn", "Venus", "Pluto", "Vulcan", "Ceres", "Apollo", "Minerva", "Diana", "Mars", "Mercury", "Bacchus", "Proserpine", "Cupid", "Terra", "Somnus", "Ops", "Uranus", "Victoria", "Aurora", "Faunus", "Luna", "Sol", "Hercules", "Ulysses"};
	private static final String[] lastnameSweden = new String[] {"Andersson", "Johansson", "Karlsson", "Nilsson", "Eriksson", "Larsson", "Olsson", "Persson", "Svensson", "Gustafsson", "Pettersson", "Jonsson", "Jansson", "Hansson", "Bengtsson", "Joensson", "Lindberg", "Jakobsson", "Magnusson", "Olofsson", "Lindstroem", "Lindqvist", "Lindgren", "Axelsson", "Berg", "Bergstroem", "Lundberg", "Lind", "Lundgren", "Lundqvist", "Mattsson", "Berglund", "Fredriksson", "Sandberg", "Henriksson", "Forsberg", "Sjoeberg", "Wallin", "Engstroem", "Eklund", "Danielsson", "Lundin", "Hakansson", "Bjoerk", "Bergman", "Gunnarsson", "Holm", "Wikstroem", "Samuelsson", "Isaksson", "Fransson", "Bergqvist", "Nystroem", "Holmberg", "Arvidsson", "Loefgren", "Soederberg", "Nyberg", "Blomqvist", "Claesson", "Nordstroem", "Martensson", "Lundstroem", "Viklund", "Bjoerklund", "Eliasson"};
	private static final String[] mythicalLocations = new String[] {"Agartha", "Alfheim", "Alomkik", "Annwn", "Amaravati", "Arcadia", "Asgard", "Asphodel Meadows", "Atlantis", "Avalon", "Axis Mundi", "Ayotha Amirtha Gangai", "Aztlan", "Baltia", "Biarmaland", "Biringan City", "Brahmapura", "Brittia", "Camelot", "City of the Caesars", "Cloud cuckoo land", "Cockaigne", "Dinas Affaraon", "Ffaraon", "Diyu", "El Dorado", "Elysian Fields", "Feather Mountain", "Garden of Eden", "Garden of the Hesperides", "Finias", "Hawaiki", "Heaven", "Hell", "Hyperborea", "Irkalla", "Islands of the Blessed", "Jabulqa", "Jambudvīpa", "Jotunheim", "Ketumati", "Kingdom of Reynes", "Kingdom of Saguenay", "Kitezh", "Kolob", "Kunlun Mountain", "Kvenland", "Kyoepelinvuori", "La Ciudad Blanca", "Laestrygon", "Lake Parime", "Land of Manu", "Lemuria", "Lintukoto", "Lyonesse", "Mag Mell", "Meropis", "Mictlan", "Mount Penglai", "Mu", "Muspelheim", "Naraka", "New Jerusalem", "Nibiru", "Niflheim", "Niflhel", "Nirvana", "Norumbega", "Nysa", "Olympus", "Paititi", "Panchaia", "Pangaia", "Pandaemonium", "Pleroma", "Pohjola", "Purgatory", "Quivira", "Cíbola", "Ram Setu", "Samavasarana", "Scholomance", "Sierra de la Plata", "Shambhala", "Shangri-La", "Suddene", "Summerland", "Svarga", "Svartalfaheimr", "Takama-ga-hara", "Tartarus", "Themiscyra", "Thule", "Thuvaraiyam Pathi", "Tir na nag", "Vaikuntha", "Valhalla", "Vanaheimr", "Westernesse", "Xanadu", "Shangdu", "Xibalba", "Yomi", "Ys", "Zarahemla", "Zerzura", "Zion"};
	private static final String[] colorNames = new String[] { "Red", "Orange", "Yellow", "Cyan", "Green", "Blue", "Pink", "Purple", "Gold", "Silver", "Bronze" };
	private static final String[] fruitNames = new String[] { "Apple", "Pear", "Orange", "Banana", "Pineapple", "Watermelon", "Grapefruit", "Papaya", "Mango", "Pomegranate", "Lemon", "Cherry", "Apricot", "Peach", "Strawberry", "Plum"};
	private static final String[] italianDesserts = new String[] { "Tiramisu", "Panna Cotta", "Gelato", "Panettone", "Biscotti", "Bombolone", "Colomba di Pasqua", "Confetti", "Frutta Martorana", "Gianduiotto", "Mustacciuoli", "Nutella", "Pandoro", "Pasticciotto", "Ricciarelli", "Semifreddo", "Sanguinaccio Dolce", "Sfogliatella", "Struffoli", "Tartufo", "Torrone", "Torta alla Monferrina", "Torta Tre Monti", "Taralli", "Uovo sbattuto", "Zabaione", "Zuccotto"};
	private static final String[] exaggeratingAdjectives = new String[] { "utterly arduous", "superfluous", "chocolate-addicted", "super-sneaky", "ultra cuddly", "mega religious", "totally angry", "absolutely arrogant", "totally-at-the-ready", "bat-sh*t-crazy", "bull-headed", "100% confused", "fully-cruel-hearted", "over-demanding", "fiercely loyal", "endlessly flirting", "free-loading", "frisky", "god-mode-greedy", "devil-like hateful", "house-broken", "above hyperactive", "high-end", "idiotic", "infuriating", "awfully insecure", "hilariously maniacal", "ultra narrow-minded", "out-of-control", "rebellious", "self-absorbed", "shaky", "shivering", "slippery", "stubborn", "territorial", "tripping", "twisted", "underhanded", "vengeful", "vile", "yapping", "zippy", "zombie-like" };
	private static final String[] ultimateServiceNames = new String[] { "OmniKoore", "Ultima-X", "Zepress-3000", "Hachijuhachi-88", "Samnizt-V8", "Oversharp V3", "Megalytics v2.9", "Korrasoft", "Softikrom P55", "Bro-Jekt 3.0", "OverApp Z", "Extremia 8008", "TX-ULTRA", "PlusQuam-Defect 8.7", "Expandor Type B", "Webator X2", "TotalKoondrol C55"};
	
	private static final String[] jobTitleAdjective = new String[] { "Chief", "Accredited", "Associate", "Lead", "Head", "Overlord", "King", "Serving", "Master", "Executive", "Administrative", "Principal", "President", "Senior", "Junior", "Official", "Unofficial", "Commanding", "Prime", "Sovereign", "Majestic", "Apocalyptic", "Magical", "Hyperactive", "Over-Emotional", "Flirting", "Vanishing", "Running", "Professional", "Academic", "Lost", "Aspiring", "Global", "Vital", "Digital", "Representative", "Organized", "Empowering", "Logical", "Paranoid", "Lunatic", "Mental", "Trustworthy", "Interim", "Emergency", "Online", "Captain", "Boss", "Creative", "Certified", "Judging", "Clueless", "Transparent", "Active", "Neglected", "Satisfied", "Hungry", "Impervious", "Causative", "Effective", "Abstract", "Invasive" };
	private static final String[] jobTitleTopic = new String[] { "Performance", "Infrastructure", "Facility", "Communication", "Executive", "Establishment", "Machine", "Cooperation", "Quality", "Tiramisu", "Technology", "Nutrition", "Treasury", "Dissemination", "Promotion", "Research", "Discovery", "Management", "Area", "Underground", "Heaven", "Hell", "Paradise", "Underworld", "Cleaning", "Screaming", "Information", "Marketing", "Business", "Sales", "Inspiration", "Operations", "Happiness", "Production", "Organization", "Computer", "Unicorn", "Food", "Health", "Workaround", "Coping", "Translation", "Catastrophe", "Security", "Software", "Conversation", "Media", "Internet", "Amazement", "Cheerleader", "Happiness", "Revenue", "Money", "Cream", "Dream", "Steam", "Realm", "Arts", "Crafts", "Army", "Troop"};
	private static final String[] jobTitleRole = new String[] { "Secretary", "Director", "Officer", "Deputy", "In-Charge", "Manager", "Expert", "Engineer", "Executive", "Architect", "Communicator", "Observer", "Neglector", "Slave", "Expeditor", "Locator", "Searcher", "Researcher", "Enabler", "Administrator", "Commander", "Leader", "Minister", "Ambassador", "Magician", "Accelerator", "Soldier", "Wizard", "Cleaner", "Creator", "Maker", "Slacker", "Idler", "Loafer", "Cruncher", "God", "Demi-God", "Editor", "Destroyer", "Collector", "Educator", "Representative", "Handshaker", "Hero", "Generalist", "Genius", "Organizator", "Whisperer", "Emperor", "Registrar", "Supervisor", "Ninja", "Inventor", "Listener", "Troublemaker", "Maestro", "Virtuoso", "Alchemist", "Designer", "Technician", "Commander", "Practitioner" };

	private static final String[] statisticsTitleAdjective = new String[] { "Heavy", "Low", "Lovely", "Tasty", "Strong", "Weak", "Enslaved", "Holy", "Spiritual", "Technical", "Digital", "Online", "Virtual", "Real", "Invented", "Shocked", "Cute", "Perfect", "Arduous", "Magical", "Annoying", "Beautiful", "Enraged", "Fallen", "Married", "Dissappointed", "Lost", "Fierce", "Idle", "Heavenly", "Hellish", "Horrifying" };
	private static final String[] statisticsTitleObject = new String[] { "Letters", "People", "Apples", "Tiramisus", "Pillows", "Pens", "Contracts", "Customers", "Patients", "Chairs", "Castles", "Dragons", "Fairies", "Gnomes", "Chemicals", "Elves", "Horses", "Magicians", "Spirits", "Trees", "Books", "Backpacks", "Angels", "Gods", "Devils", "Demons", "Heros", "Soldiers", "Papers", "Games", "Rooms", "Liquids", "Materials" };
	private static final String[] statisticsTitleVerb = new String[] { "Sent", "Written", "Glued", "Woreshipped", "Banned", "Cuddled", "Destroyed", "Hidden", "Stolen", "Horrified", "Made", "Created", "Impressed", "Fortified", "Mixed", "Hunted", "Befriended", "Educated", "Bewitched", "Cursed", "Enchanted", "Backed", "Sold", "Combusted", "Briefed", "Registered", "Educated", "Eradicated", "Engaged", "Divorced", "Played", "Tested", "Documented" };
	
	private static final String[] methodNamePrefix = new String[] {"get", "set", "create", "read", "update", "delete", "execute", "call", "generate", "make", "rename", "add", "remove", "copy", "duplicate", "put", "push", "check", "is", "has", "can", "format", "convert", "send", "poll", "sort", "filter", "decode", "encode", "compress", "decompress", "zip", "unzip", "encrypt", "decrypt", "calculate", "calc", "give", "gimme", "hide", "reveal", "regurgitate", "accrue", "iterate", "write", "recreate", "permit", "levitate", "enchant", "bewitch", "slay", "cast", "refer", "hunt", "find", "conquer", "imperialize", "holify", "deify", "revere", "arise", "awaken", "enshroud", "encroach", "trap", "regurgitate", "master", "train", "enact", "exhibit", "engulf", "overthrow", "acquire", "steal", "shoot", "empower", "embolden", "heal", "revive", "zombify", "persuade"};
	private static final String[] methodNameItem = new String[] {"Customer", "Account", "Table", "Method", "Statistics", "Cell", "Maximum", "Message", "Record", "Category", "Tiramisu", "Source", "Box", "Package", "Graph", "Angel", "Demon", "Perfect", "Digital", "Virtual", "Artificial", "Actual", "First", "Last", "Literal", "Parameter", "Clock", "Berserk", "Magic", "Comment", "Page", "Test", "Money", "Cash", "Energy", "Wave", "Billing", "Cart", "Shop", "Image", "Game", "Taste", "Interest", "Gravity", "Heaven", "Scrumptious", "Royal", "Noble", "Crossbow", "Gunpowder", "Marvelous", "Extraordinary", "Fantastabulous", "Spiffy", "Stealth", "Corroded", "Corrupted", "Deteriorated", "Immoral", "Sabotaged", "Liquified"};
	private static final String[] methodNameSuffix = new String[] {"Count", "Value", "Time", "Role", "Permissions", "Average", "Globals", "Metadata", "Defaults", "Height", "Width", "Dimensions", "Length", "Size", "Activity", "Copy", "Chart", "Extremes", "Overlaps", "Caches", "Rights", "Interval", "Set", "Map", "List", "Collection", "Array", "Object", "Results", "Number", "Status", "Percentage", "Consumption", "Balance", "Time", "Entry", "Row", "Column", "Performance", "Load", "Rate", "Allowance", "Beast", "Angel", "Smithereens", "Priestess", "Sorceress", "Kingdom", "Fishes", "Stars", "Furnance", "Sword", "Spear", "Assassin", "Mage", "Huntress", "Fisherman", "Duchess", "Lord", "King", "Prince", "Waitress", "Enemies", "Sirens", "Overlords", "Overlady", "Empire", "Carpenter", "Phoenix", "Blacksmith", "Zombies", "Ghouls", "Kobolds", "Endeavours" };

	private static final String[] companyTitleFirst = new String[] { "Rockford", "Battlefield", "First", "Second", "Ultimate", "Omega", "Best", "Expensive", "Luxus", "Traders", "More", "Over", "Super", "Allyn", "O'Sullivan", "O'Brian", "O'Connor", "O'Deorian", "McGregor", "Sister's", "Brother's", "Mother's", "Father's", "My", "Expert", "Cheap", "Heavy", "Virtual", "Internet", "War", "Real", "Unreal", "Fairy", "Dragon", "Dungeon", "Slave", "Master", "Elves", "Goblin", "Wyvern", "Centaur", "Minotaur", "Dwarven", "Custom", "Lamia", "Pixie", "Demon", "Angel", "Rocker", "Magician", "Knight", "Sorceress", "Lizardman", "Mermaid", "Zeus", "Goddess", "Mythical", "Magical", "Fantastic" };
	private static final String[] companyTitleSecond = new String[] { "Wood", "Plastics", "Metals", "Silver", "Gold", "Platinum", "Ceramics", "Fun", "Brothers", "Sisters", "and Family", "and Daughters", "and Sons", "and Mothers", "and Father's", "IT", "Digital", "Backery", "Industry", "Supermarket", "Trading", "Finance", "Army", "Weapons", "Games", "Gaming", "Packaging", "Technology", "Care", "Health", "Computer", "Specialist", "Printing", "3D", "Stealth", "Helicopter", "Aircraft", "Archeology", "Astronomy", "Geology", "Mathematics", "Language", "Housing", "Fabrics", "Clothing", "Underwear", "Toys", "Insanity", "Estate", "Lumber", "Meat", "Fruit", "Shepherd", "Dogs", "Cats", "Parrots", "Animals", "Lamas", "Elefants", "Restaurant", "Take-Away", "Food", "Marriage", "Stories", "City", "Tools", "Crafting", "Dirt", "Cleaning", "Dust", "Soil", "Fertilizer", "Litterbox", "Security", "Guard", "Floor", "Cooking", "Extermination", "Book"};
	private static final String[] companyTitleThird = new String[] { "AG", "GmbH", "Inc.", "Ltd.", "LCC", "PLCC", "Corp.", "Co-Op", "S.A.", "S.L.", "Business", "Company", "Group", "Corporation", "Services", "Shop", "Store", "School", "University", "Counselor", "Solicitors", "Trader", "Industry", "Industries", "Agency"};
	
	private static final JsonArray countryData = (JsonArray)GatlytronJson.fromJson("[{Country:'South Georgia and South Sandwich Islands',Capital:'King Edward Point',CapitalLatitude:-54.283333,CapitalLongitude:-36.5,CountryCode:'GS',Continent:'Antarctica'},{Country:'French Southern and Antarctic Lands',Capital:'Port-aux-Francais',CapitalLatitude:-49.35,CapitalLongitude:70.216667,CountryCode:'TF',Continent:'Antarctica'},{Country:'Palestine',Capital:'Jerusalem',CapitalLatitude:31.766666666666666,CapitalLongitude:35.233333,CountryCode:'PS',Continent:'Asia'},{Country:'Aland Islands',Capital:'Mariehamn',CapitalLatitude:60.116667,CapitalLongitude:19.9,CountryCode:'AX',Continent:'Europe'},{Country:'Nauru',Capital:'Yaren',CapitalLatitude:-0.5477,CapitalLongitude:166.920867,CountryCode:'NR',Continent:'Australia'},{Country:'Saint Martin',Capital:'Marigot',CapitalLatitude:18.0731,CapitalLongitude:-63.0822,CountryCode:'MF',Continent:'North America'},{Country:'Tokelau',Capital:'Atafu',CapitalLatitude:-9.166667,CapitalLongitude:-171.833333,CountryCode:'TK',Continent:'Australia'},{Country:'Western Sahara',Capital:'El-AaiÃºn',CapitalLatitude:27.153611,CapitalLongitude:-13.203333,CountryCode:'EH',Continent:'Africa'},{Country:'Afghanistan',Capital:'Kabul',CapitalLatitude:34.516666666666666,CapitalLongitude:69.183333,CountryCode:'AF',Continent:'Asia'},{Country:'Albania',Capital:'Tirana',CapitalLatitude:41.31666666666667,CapitalLongitude:19.816667,CountryCode:'AL',Continent:'Europe'},{Country:'Algeria',Capital:'Algiers',CapitalLatitude:36.75,CapitalLongitude:3.05,CountryCode:'DZ',Continent:'Africa'},{Country:'American Samoa',Capital:'Pago Pago',CapitalLatitude:-14.266666666666667,CapitalLongitude:-170.7,CountryCode:'AS',Continent:'Australia'},{Country:'Andorra',Capital:'Andorra la Vella',CapitalLatitude:42.5,CapitalLongitude:1.516667,CountryCode:'AD',Continent:'Europe'},{Country:'Angola',Capital:'Luanda',CapitalLatitude:-8.833333333333334,CapitalLongitude:13.216667,CountryCode:'AO',Continent:'Africa'},{Country:'Anguilla',Capital:'The Valley',CapitalLatitude:18.216666666666665,CapitalLongitude:-63.05,CountryCode:'AI',Continent:'North America'},{Country:'Antigua and Barbuda',Capital:\"Saint John's\",CapitalLatitude:17.116666666666667,CapitalLongitude:-61.85,CountryCode:'AG',Continent:'North America'},{Country:'Argentina',Capital:'Buenos Aires',CapitalLatitude:-34.583333333333336,CapitalLongitude:-58.666667,CountryCode:'AR',Continent:'South America'},{Country:'Armenia',Capital:'Yerevan',CapitalLatitude:40.166666666666664,CapitalLongitude:44.5,CountryCode:'AM',Continent:'Europe'},{Country:'Aruba',Capital:'Oranjestad',CapitalLatitude:12.516666666666667,CapitalLongitude:-70.033333,CountryCode:'AW',Continent:'North America'},{Country:'Australia',Capital:'Canberra',CapitalLatitude:-35.266666666666666,CapitalLongitude:149.133333,CountryCode:'AU',Continent:'Australia'},{Country:'Austria',Capital:'Vienna',CapitalLatitude:48.2,CapitalLongitude:16.366667,CountryCode:'AT',Continent:'Europe'},{Country:'Azerbaijan',Capital:'Baku',CapitalLatitude:40.38333333333333,CapitalLongitude:49.866667,CountryCode:'AZ',Continent:'Europe'},{Country:'Bahamas',Capital:'Nassau',CapitalLatitude:25.083333333333332,CapitalLongitude:-77.35,CountryCode:'BS',Continent:'North America'},{Country:'Bahrain',Capital:'Manama',CapitalLatitude:26.233333333333334,CapitalLongitude:50.566667,CountryCode:'BH',Continent:'Asia'},{Country:'Bangladesh',Capital:'Dhaka',CapitalLatitude:23.716666666666665,CapitalLongitude:90.4,CountryCode:'BD',Continent:'Asia'},{Country:'Barbados',Capital:'Bridgetown',CapitalLatitude:13.1,CapitalLongitude:-59.616667,CountryCode:'BB',Continent:'North America'},{Country:'Belarus',Capital:'Minsk',CapitalLatitude:53.9,CapitalLongitude:27.566667,CountryCode:'BY',Continent:'Europe'},{Country:'Belgium',Capital:'Brussels',CapitalLatitude:50.833333333333336,CapitalLongitude:4.333333,CountryCode:'BE',Continent:'Europe'},{Country:'Belize',Capital:'Belmopan',CapitalLatitude:17.25,CapitalLongitude:-88.766667,CountryCode:'BZ',Continent:'Central America'},{Country:'Somaliland',Capital:'Hargeisa',CapitalLatitude:9.55,CapitalLongitude:44.05,CountryCode:'NULL',Continent:'Africa'},{Country:'Benin',Capital:'Porto-Novo',CapitalLatitude:6.483333333333333,CapitalLongitude:2.616667,CountryCode:'BJ',Continent:'Africa'},{Country:'Bermuda',Capital:'Hamilton',CapitalLatitude:32.28333333333333,CapitalLongitude:-64.783333,CountryCode:'BM',Continent:'North America'},{Country:'Bhutan',Capital:'Thimphu',CapitalLatitude:27.466666666666665,CapitalLongitude:89.633333,CountryCode:'BT',Continent:'Asia'},{Country:'Bolivia',Capital:'La Paz',CapitalLatitude:-16.5,CapitalLongitude:-68.15,CountryCode:'BO',Continent:'South America'},{Country:'Bosnia and Herzegovina',Capital:'Sarajevo',CapitalLatitude:43.86666666666667,CapitalLongitude:18.416667,CountryCode:'BA',Continent:'Europe'},{Country:'Botswana',Capital:'Gaborone',CapitalLatitude:-24.633333333333333,CapitalLongitude:25.9,CountryCode:'BW',Continent:'Africa'},{Country:'Brazil',Capital:'Brasilia',CapitalLatitude:-15.783333333333333,CapitalLongitude:-47.916667,CountryCode:'BR',Continent:'South America'},{Country:'British Virgin Islands',Capital:'Road Town',CapitalLatitude:18.416666666666668,CapitalLongitude:-64.616667,CountryCode:'VG',Continent:'North America'},{Country:'Brunei Darussalam',Capital:'Bandar Seri Begawan',CapitalLatitude:4.883333333333333,CapitalLongitude:114.933333,CountryCode:'BN',Continent:'Asia'},{Country:'Bulgaria',Capital:'Sofia',CapitalLatitude:42.68333333333333,CapitalLongitude:23.316667,CountryCode:'BG',Continent:'Europe'},{Country:'Burkina Faso',Capital:'Ouagadougou',CapitalLatitude:12.366666666666667,CapitalLongitude:-1.516667,CountryCode:'BF',Continent:'Africa'},{Country:'Myanmar',Capital:'Rangoon',CapitalLatitude:16.8,CapitalLongitude:96.15,CountryCode:'MM',Continent:'Asia'},{Country:'Burundi',Capital:'Bujumbura',CapitalLatitude:-3.3666666666666667,CapitalLongitude:29.35,CountryCode:'BI',Continent:'Africa'},{Country:'Cambodia',Capital:'Phnom Penh',CapitalLatitude:11.55,CapitalLongitude:104.916667,CountryCode:'KH',Continent:'Asia'},{Country:'Cameroon',Capital:'Yaounde',CapitalLatitude:3.8666666666666667,CapitalLongitude:11.516667,CountryCode:'CM',Continent:'Africa'},{Country:'Canada',Capital:'Ottawa',CapitalLatitude:45.416666666666664,CapitalLongitude:-75.7,CountryCode:'CA',Continent:'Central America'},{Country:'Cape Verde',Capital:'Praia',CapitalLatitude:14.916666666666666,CapitalLongitude:-23.516667,CountryCode:'CV',Continent:'Africa'},{Country:'Cayman Islands',Capital:'George Town',CapitalLatitude:19.3,CapitalLongitude:-81.383333,CountryCode:'KY',Continent:'North America'},{Country:'Central African Republic',Capital:'Bangui',CapitalLatitude:4.366666666666666,CapitalLongitude:18.583333,CountryCode:'CF',Continent:'Africa'},{Country:'Chad',Capital:\"N'Djamena\",CapitalLatitude:12.1,CapitalLongitude:15.033333,CountryCode:'TD',Continent:'Africa'},{Country:'Chile',Capital:'Santiago',CapitalLatitude:-33.45,CapitalLongitude:-70.666667,CountryCode:'CL',Continent:'South America'},{Country:'China',Capital:'Beijing',CapitalLatitude:39.916666666666664,CapitalLongitude:116.383333,CountryCode:'CN',Continent:'Asia'},{Country:'Christmas Island',Capital:'The Settlement',CapitalLatitude:-10.416666666666666,CapitalLongitude:105.716667,CountryCode:'CX',Continent:'Australia'},{Country:'Cocos Islands',Capital:'West Island',CapitalLatitude:-12.166666666666666,CapitalLongitude:96.833333,CountryCode:'CC',Continent:'Australia'},{Country:'Colombia',Capital:'Bogota',CapitalLatitude:4.6,CapitalLongitude:-74.083333,CountryCode:'CO',Continent:'South America'},{Country:'Comoros',Capital:'Moroni',CapitalLatitude:-11.7,CapitalLongitude:43.233333,CountryCode:'KM',Continent:'Africa'},{Country:'Democratic Republic of the Congo',Capital:'Kinshasa',CapitalLatitude:-4.316666666666666,CapitalLongitude:15.3,CountryCode:'CD',Continent:'Africa'},{Country:'Republic of Congo',Capital:'Brazzaville',CapitalLatitude:-4.25,CapitalLongitude:15.283333,CountryCode:'CG',Continent:'Africa'},{Country:'Cook Islands',Capital:'Avarua',CapitalLatitude:-21.2,CapitalLongitude:-159.766667,CountryCode:'CK',Continent:'Australia'},{Country:'Costa Rica',Capital:'San Jose',CapitalLatitude:9.933333333333334,CapitalLongitude:-84.083333,CountryCode:'CR',Continent:'Central America'},{Country:\"Cote d'Ivoire\",Capital:'Yamoussoukro',CapitalLatitude:6.816666666666666,CapitalLongitude:-5.266667,CountryCode:'CI',Continent:'Africa'},{Country:'Croatia',Capital:'Zagreb',CapitalLatitude:45.8,CapitalLongitude:16.0,CountryCode:'HR',Continent:'Europe'},{Country:'Cuba',Capital:'Havana',CapitalLatitude:23.116666666666667,CapitalLongitude:-82.35,CountryCode:'CU',Continent:'North America'},{Country:'CuraÃ§ao',Capital:'Willemstad',CapitalLatitude:12.1,CapitalLongitude:-68.916667,CountryCode:'CW',Continent:'North America'},{Country:'Cyprus',Capital:'Nicosia',CapitalLatitude:35.166666666666664,CapitalLongitude:33.366667,CountryCode:'CY',Continent:'Europe'},{Country:'Czech Republic',Capital:'Prague',CapitalLatitude:50.083333333333336,CapitalLongitude:14.466667,CountryCode:'CZ',Continent:'Europe'},{Country:'Denmark',Capital:'Copenhagen',CapitalLatitude:55.666666666666664,CapitalLongitude:12.583333,CountryCode:'DK',Continent:'Europe'},{Country:'Djibouti',Capital:'Djibouti',CapitalLatitude:11.583333333333334,CapitalLongitude:43.15,CountryCode:'DJ',Continent:'Africa'},{Country:'Dominica',Capital:'Roseau',CapitalLatitude:15.3,CapitalLongitude:-61.4,CountryCode:'DM',Continent:'North America'},{Country:'Dominican Republic',Capital:'Santo Domingo',CapitalLatitude:18.466666666666665,CapitalLongitude:-69.9,CountryCode:'DO',Continent:'North America'},{Country:'Ecuador',Capital:'Quito',CapitalLatitude:-0.21666666666666667,CapitalLongitude:-78.5,CountryCode:'EC',Continent:'South America'},{Country:'Egypt',Capital:'Cairo',CapitalLatitude:30.05,CapitalLongitude:31.25,CountryCode:'EG',Continent:'Africa'},{Country:'El Salvador',Capital:'San Salvador',CapitalLatitude:13.7,CapitalLongitude:-89.2,CountryCode:'SV',Continent:'Central America'},{Country:'Equatorial Guinea',Capital:'Malabo',CapitalLatitude:3.75,CapitalLongitude:8.783333,CountryCode:'GQ',Continent:'Africa'},{Country:'Eritrea',Capital:'Asmara',CapitalLatitude:15.333333333333334,CapitalLongitude:38.933333,CountryCode:'ER',Continent:'Africa'},{Country:'Estonia',Capital:'Tallinn',CapitalLatitude:59.43333333333333,CapitalLongitude:24.716667,CountryCode:'EE',Continent:'Europe'},{Country:'Ethiopia',Capital:'Addis Ababa',CapitalLatitude:9.033333333333333,CapitalLongitude:38.7,CountryCode:'ET',Continent:'Africa'},{Country:'Falkland Islands',Capital:'Stanley',CapitalLatitude:-51.7,CapitalLongitude:-57.85,CountryCode:'FK',Continent:'South America'},{Country:'Faroe Islands',Capital:'Torshavn',CapitalLatitude:62,CapitalLongitude:-6.766667,CountryCode:'FO',Continent:'Europe'},{Country:'Fiji',Capital:'Suva',CapitalLatitude:-18.133333333333333,CapitalLongitude:178.416667,CountryCode:'FJ',Continent:'Australia'},{Country:'Finland',Capital:'Helsinki',CapitalLatitude:60.166666666666664,CapitalLongitude:24.933333,CountryCode:'FI',Continent:'Europe'},{Country:'France',Capital:'Paris',CapitalLatitude:48.86666666666667,CapitalLongitude:2.333333,CountryCode:'FR',Continent:'Europe'},{Country:'French Polynesia',Capital:'Papeete',CapitalLatitude:-17.533333333333335,CapitalLongitude:-149.566667,CountryCode:'PF',Continent:'Australia'},{Country:'Gabon',Capital:'Libreville',CapitalLatitude:0.38333333333333336,CapitalLongitude:9.45,CountryCode:'GA',Continent:'Africa'},{Country:'The Gambia',Capital:'Banjul',CapitalLatitude:13.45,CapitalLongitude:-16.566667,CountryCode:'GM',Continent:'Africa'},{Country:'Georgia',Capital:'Tbilisi',CapitalLatitude:41.68333333333333,CapitalLongitude:44.833333,CountryCode:'GE',Continent:'Europe'},{Country:'Germany',Capital:'Berlin',CapitalLatitude:52.516666666666666,CapitalLongitude:13.4,CountryCode:'DE',Continent:'Europe'},{Country:'Ghana',Capital:'Accra',CapitalLatitude:5.55,CapitalLongitude:-0.216667,CountryCode:'GH',Continent:'Africa'},{Country:'Gibraltar',Capital:'Gibraltar',CapitalLatitude:36.13333333333333,CapitalLongitude:-5.35,CountryCode:'GI',Continent:'Europe'},{Country:'Greece',Capital:'Athens',CapitalLatitude:37.983333333333334,CapitalLongitude:23.733333,CountryCode:'GR',Continent:'Europe'},{Country:'Greenland',Capital:'Nuuk',CapitalLatitude:64.18333333333334,CapitalLongitude:-51.75,CountryCode:'GL',Continent:'Central America'},{Country:'Grenada',Capital:\"Saint George's\",CapitalLatitude:12.05,CapitalLongitude:-61.75,CountryCode:'GD',Continent:'North America'},{Country:'Guam',Capital:'Hagatna',CapitalLatitude:13.466666666666667,CapitalLongitude:144.733333,CountryCode:'GU',Continent:'Australia'},{Country:'Guatemala',Capital:'Guatemala City',CapitalLatitude:14.616666666666667,CapitalLongitude:-90.516667,CountryCode:'GT',Continent:'Central America'},{Country:'Guernsey',Capital:'Saint Peter Port',CapitalLatitude:49.45,CapitalLongitude:-2.533333,CountryCode:'GG',Continent:'Europe'},{Country:'Guinea',Capital:'Conakry',CapitalLatitude:9.5,CapitalLongitude:-13.7,CountryCode:'GN',Continent:'Africa'},{Country:'Guinea-Bissau',Capital:'Bissau',CapitalLatitude:11.85,CapitalLongitude:-15.583333,CountryCode:'GW',Continent:'Africa'},{Country:'Guyana',Capital:'Georgetown',CapitalLatitude:6.8,CapitalLongitude:-58.15,CountryCode:'GY',Continent:'South America'},{Country:'Haiti',Capital:'Port-au-Prince',CapitalLatitude:18.533333333333335,CapitalLongitude:-72.333333,CountryCode:'HT',Continent:'North America'},{Country:'Vatican City',Capital:'Vatican City',CapitalLatitude:41.9,CapitalLongitude:12.45,CountryCode:'VA',Continent:'Europe'},{Country:'Honduras',Capital:'Tegucigalpa',CapitalLatitude:14.1,CapitalLongitude:-87.216667,CountryCode:'HN',Continent:'Central America'},{Country:'Hungary',Capital:'Budapest',CapitalLatitude:47.5,CapitalLongitude:19.083333,CountryCode:'HU',Continent:'Europe'},{Country:'Iceland',Capital:'Reykjavik',CapitalLatitude:64.15,CapitalLongitude:-21.95,CountryCode:'IS',Continent:'Europe'},{Country:'India',Capital:'New Delhi',CapitalLatitude:28.6,CapitalLongitude:77.2,CountryCode:'IN',Continent:'Asia'},{Country:'Indonesia',Capital:'Jakarta',CapitalLatitude:-6.166666666666667,CapitalLongitude:106.816667,CountryCode:'ID',Continent:'Asia'},{Country:'Iran',Capital:'Tehran',CapitalLatitude:35.7,CapitalLongitude:51.416667,CountryCode:'IR',Continent:'Asia'},{Country:'Iraq',Capital:'Baghdad',CapitalLatitude:33.333333333333336,CapitalLongitude:44.4,CountryCode:'IQ',Continent:'Asia'},{Country:'Ireland',Capital:'Dublin',CapitalLatitude:53.31666666666667,CapitalLongitude:-6.233333,CountryCode:'IE',Continent:'Europe'},{Country:'Isle of Man',Capital:'Douglas',CapitalLatitude:54.15,CapitalLongitude:-4.483333,CountryCode:'IM',Continent:'Europe'},{Country:'Israel',Capital:'Jerusalem',CapitalLatitude:31.766666666666666,CapitalLongitude:35.233333,CountryCode:'IL',Continent:'Asia'},{Country:'Italy',Capital:'Rome',CapitalLatitude:41.9,CapitalLongitude:12.483333,CountryCode:'IT',Continent:'Europe'},{Country:'Jamaica',Capital:'Kingston',CapitalLatitude:18,CapitalLongitude:-76.8,CountryCode:'JM',Continent:'North America'},{Country:'Japan',Capital:'Tokyo',CapitalLatitude:35.68333333333333,CapitalLongitude:139.75,CountryCode:'JP',Continent:'Asia'},{Country:'Jersey',Capital:'Saint Helier',CapitalLatitude:49.18333333333333,CapitalLongitude:-2.1,CountryCode:'JE',Continent:'Europe'},{Country:'Jordan',Capital:'Amman',CapitalLatitude:31.95,CapitalLongitude:35.933333,CountryCode:'JO',Continent:'Asia'},{Country:'Kazakhstan',Capital:'Astana',CapitalLatitude:51.166666666666664,CapitalLongitude:71.416667,CountryCode:'KZ',Continent:'Asia'},{Country:'Kenya',Capital:'Nairobi',CapitalLatitude:-1.2833333333333332,CapitalLongitude:36.816667,CountryCode:'KE',Continent:'Africa'},{Country:'Kiribati',Capital:'Tarawa',CapitalLatitude:-0.8833333333333333,CapitalLongitude:169.533333,CountryCode:'KI',Continent:'Australia'},{Country:'North Korea',Capital:'Pyongyang',CapitalLatitude:39.016666666666666,CapitalLongitude:125.75,CountryCode:'KP',Continent:'Asia'},{Country:'South Korea',Capital:'Seoul',CapitalLatitude:37.55,CapitalLongitude:126.983333,CountryCode:'KR',Continent:'Asia'},{Country:'Kosovo',Capital:'Pristina',CapitalLatitude:42.666666666666664,CapitalLongitude:21.166667,CountryCode:'KO',Continent:'Europe'},{Country:'Kuwait',Capital:'Kuwait City',CapitalLatitude:29.366666666666667,CapitalLongitude:47.966667,CountryCode:'KW',Continent:'Asia'},{Country:'Kyrgyzstan',Capital:'Bishkek',CapitalLatitude:42.86666666666667,CapitalLongitude:74.6,CountryCode:'KG',Continent:'Asia'},{Country:'Laos',Capital:'Vientiane',CapitalLatitude:17.966666666666665,CapitalLongitude:102.6,CountryCode:'LA',Continent:'Asia'},{Country:'Latvia',Capital:'Riga',CapitalLatitude:56.95,CapitalLongitude:24.1,CountryCode:'LV',Continent:'Europe'},{Country:'Lebanon',Capital:'Beirut',CapitalLatitude:33.86666666666667,CapitalLongitude:35.5,CountryCode:'LB',Continent:'Asia'},{Country:'Lesotho',Capital:'Maseru',CapitalLatitude:-29.316666666666666,CapitalLongitude:27.483333,CountryCode:'LS',Continent:'Africa'},{Country:'Liberia',Capital:'Monrovia',CapitalLatitude:6.3,CapitalLongitude:-10.8,CountryCode:'LR',Continent:'Africa'},{Country:'Libya',Capital:'Tripoli',CapitalLatitude:32.88333333333333,CapitalLongitude:13.166667,CountryCode:'LY',Continent:'Africa'},{Country:'Liechtenstein',Capital:'Vaduz',CapitalLatitude:47.13333333333333,CapitalLongitude:9.516667,CountryCode:'LI',Continent:'Europe'},{Country:'Lithuania',Capital:'Vilnius',CapitalLatitude:54.68333333333333,CapitalLongitude:25.316667,CountryCode:'LT',Continent:'Europe'},{Country:'Luxembourg',Capital:'Luxembourg',CapitalLatitude:49.6,CapitalLongitude:6.116667,CountryCode:'LU',Continent:'Europe'},{Country:'Macedonia',Capital:'Skopje',CapitalLatitude:42,CapitalLongitude:21.433333,CountryCode:'MK',Continent:'Europe'},{Country:'Madagascar',Capital:'Antananarivo',CapitalLatitude:-18.916666666666668,CapitalLongitude:47.516667,CountryCode:'MG',Continent:'Africa'},{Country:'Malawi',Capital:'Lilongwe',CapitalLatitude:-13.966666666666667,CapitalLongitude:33.783333,CountryCode:'MW',Continent:'Africa'},{Country:'Malaysia',Capital:'Kuala Lumpur',CapitalLatitude:3.1666666666666665,CapitalLongitude:101.7,CountryCode:'MY',Continent:'Asia'},{Country:'Maldives',Capital:'Male',CapitalLatitude:4.166666666666667,CapitalLongitude:73.5,CountryCode:'MV',Continent:'Asia'},{Country:'Mali',Capital:'Bamako',CapitalLatitude:12.65,CapitalLongitude:-8.0,CountryCode:'ML',Continent:'Africa'},{Country:'Malta',Capital:'Valletta',CapitalLatitude:35.88333333333333,CapitalLongitude:14.5,CountryCode:'MT',Continent:'Europe'},{Country:'Marshall Islands',Capital:'Majuro',CapitalLatitude:7.1,CapitalLongitude:171.383333,CountryCode:'MH',Continent:'Australia'},{Country:'Mauritania',Capital:'Nouakchott',CapitalLatitude:18.066666666666666,CapitalLongitude:-15.966667,CountryCode:'MR',Continent:'Africa'},{Country:'Mauritius',Capital:'Port Louis',CapitalLatitude:-20.15,CapitalLongitude:57.483333,CountryCode:'MU',Continent:'Africa'},{Country:'Mexico',Capital:'Mexico City',CapitalLatitude:19.433333333333334,CapitalLongitude:-99.133333,CountryCode:'MX',Continent:'Central America'},{Country:'Federated States of Micronesia',Capital:'Palikir',CapitalLatitude:6.916666666666667,CapitalLongitude:158.15,CountryCode:'FM',Continent:'Australia'},{Country:'Moldova',Capital:'Chisinau',CapitalLatitude:47,CapitalLongitude:28.85,CountryCode:'MD',Continent:'Europe'},{Country:'Monaco',Capital:'Monaco',CapitalLatitude:43.733333333333334,CapitalLongitude:7.416667,CountryCode:'MC',Continent:'Europe'},{Country:'Mongolia',Capital:'Ulaanbaatar',CapitalLatitude:47.916666666666664,CapitalLongitude:106.916667,CountryCode:'MN',Continent:'Asia'},{Country:'Montenegro',Capital:'Podgorica',CapitalLatitude:42.43333333333333,CapitalLongitude:19.266667,CountryCode:'ME',Continent:'Europe'},{Country:'Montserrat',Capital:'Plymouth',CapitalLatitude:16.7,CapitalLongitude:-62.216667,CountryCode:'MS',Continent:'North America'},{Country:'Morocco',Capital:'Rabat',CapitalLatitude:34.016666666666666,CapitalLongitude:-6.816667,CountryCode:'MA',Continent:'Africa'},{Country:'Mozambique',Capital:'Maputo',CapitalLatitude:-25.95,CapitalLongitude:32.583333,CountryCode:'MZ',Continent:'Africa'},{Country:'Namibia',Capital:'Windhoek',CapitalLatitude:-22.566666666666666,CapitalLongitude:17.083333,CountryCode:'NA',Continent:'Africa'},{Country:'Nepal',Capital:'Kathmandu',CapitalLatitude:27.716666666666665,CapitalLongitude:85.316667,CountryCode:'NP',Continent:'Asia'},{Country:'Netherlands',Capital:'Amsterdam',CapitalLatitude:52.35,CapitalLongitude:4.916667,CountryCode:'NL',Continent:'Europe'},{Country:'New Caledonia',Capital:'Noumea',CapitalLatitude:-22.266666666666666,CapitalLongitude:166.45,CountryCode:'NC',Continent:'Australia'},{Country:'New Zealand',Capital:'Wellington',CapitalLatitude:-41.3,CapitalLongitude:174.783333,CountryCode:'NZ',Continent:'Australia'},{Country:'Nicaragua',Capital:'Managua',CapitalLatitude:12.133333333333333,CapitalLongitude:-86.25,CountryCode:'NI',Continent:'Central America'},{Country:'Niger',Capital:'Niamey',CapitalLatitude:13.516666666666667,CapitalLongitude:2.116667,CountryCode:'NE',Continent:'Africa'},{Country:'Nigeria',Capital:'Abuja',CapitalLatitude:9.083333333333334,CapitalLongitude:7.533333,CountryCode:'NG',Continent:'Africa'},{Country:'Niue',Capital:'Alofi',CapitalLatitude:-19.016666666666666,CapitalLongitude:-169.916667,CountryCode:'NU',Continent:'Australia'},{Country:'Norfolk Island',Capital:'Kingston',CapitalLatitude:-29.05,CapitalLongitude:167.966667,CountryCode:'NF',Continent:'Australia'},{Country:'Northern Mariana Islands',Capital:'Saipan',CapitalLatitude:15.2,CapitalLongitude:145.75,CountryCode:'MP',Continent:'Australia'},{Country:'Norway',Capital:'Oslo',CapitalLatitude:59.916666666666664,CapitalLongitude:10.75,CountryCode:'NO',Continent:'Europe'},{Country:'Oman',Capital:'Muscat',CapitalLatitude:23.616666666666667,CapitalLongitude:58.583333,CountryCode:'OM',Continent:'Asia'},{Country:'Pakistan',Capital:'Islamabad',CapitalLatitude:33.68333333333333,CapitalLongitude:73.05,CountryCode:'PK',Continent:'Asia'},{Country:'Palau',Capital:'Melekeok',CapitalLatitude:7.483333333333333,CapitalLongitude:134.633333,CountryCode:'PW',Continent:'Australia'},{Country:'Panama',Capital:'Panama City',CapitalLatitude:8.966666666666667,CapitalLongitude:-79.533333,CountryCode:'PA',Continent:'Central America'},{Country:'Papua New Guinea',Capital:'Port Moresby',CapitalLatitude:-9.45,CapitalLongitude:147.183333,CountryCode:'PG',Continent:'Australia'},{Country:'Paraguay',Capital:'Asuncion',CapitalLatitude:-25.266666666666666,CapitalLongitude:-57.666667,CountryCode:'PY',Continent:'South America'},{Country:'Peru',Capital:'Lima',CapitalLatitude:-12.05,CapitalLongitude:-77.05,CountryCode:'PE',Continent:'South America'},{Country:'Philippines',Capital:'Manila',CapitalLatitude:14.6,CapitalLongitude:120.966667,CountryCode:'PH',Continent:'Asia'},{Country:'Pitcairn Islands',Capital:'Adamstown',CapitalLatitude:-25.066666666666666,CapitalLongitude:-130.083333,CountryCode:'PN',Continent:'Australia'},{Country:'Poland',Capital:'Warsaw',CapitalLatitude:52.25,CapitalLongitude:21.0,CountryCode:'PL',Continent:'Europe'},{Country:'Portugal',Capital:'Lisbon',CapitalLatitude:38.71666666666667,CapitalLongitude:-9.133333,CountryCode:'PT',Continent:'Europe'},{Country:'Puerto Rico',Capital:'San Juan',CapitalLatitude:18.466666666666665,CapitalLongitude:-66.116667,CountryCode:'PR',Continent:'North America'},{Country:'Qatar',Capital:'Doha',CapitalLatitude:25.283333333333335,CapitalLongitude:51.533333,CountryCode:'QA',Continent:'Asia'},{Country:'Romania',Capital:'Bucharest',CapitalLatitude:44.43333333333333,CapitalLongitude:26.1,CountryCode:'RO',Continent:'Europe'},{Country:'Russia',Capital:'Moscow',CapitalLatitude:55.75,CapitalLongitude:37.6,CountryCode:'RU',Continent:'Europe'},{Country:'Rwanda',Capital:'Kigali',CapitalLatitude:-1.95,CapitalLongitude:30.05,CountryCode:'RW',Continent:'Africa'},{Country:'Saint Barthelemy',Capital:'Gustavia',CapitalLatitude:17.883333333333333,CapitalLongitude:-62.85,CountryCode:'BL',Continent:'North America'},{Country:'Saint Helena',Capital:'Jamestown',CapitalLatitude:-15.933333333333334,CapitalLongitude:-5.716667,CountryCode:'SH',Continent:'Africa'},{Country:'Saint Kitts and Nevis',Capital:'Basseterre',CapitalLatitude:17.3,CapitalLongitude:-62.716667,CountryCode:'KN',Continent:'North America'},{Country:'Saint Lucia',Capital:'Castries',CapitalLatitude:14,CapitalLongitude:-61.0,CountryCode:'LC',Continent:'North America'},{Country:'Saint Pierre and Miquelon',Capital:'Saint-Pierre',CapitalLatitude:46.766666666666666,CapitalLongitude:-56.183333,CountryCode:'PM',Continent:'Central America'},{Country:'Saint Vincent and the Grenadines',Capital:'Kingstown',CapitalLatitude:13.133333333333333,CapitalLongitude:-61.216667,CountryCode:'VC',Continent:'Central America'},{Country:'Samoa',Capital:'Apia',CapitalLatitude:-13.816666666666666,CapitalLongitude:-171.766667,CountryCode:'WS',Continent:'Australia'},{Country:'San Marino',Capital:'San Marino',CapitalLatitude:43.93333333333333,CapitalLongitude:12.416667,CountryCode:'SM',Continent:'Europe'},{Country:'Sao Tome and Principe',Capital:'Sao Tome',CapitalLatitude:0.3333333333333333,CapitalLongitude:6.733333,CountryCode:'ST',Continent:'Africa'},{Country:'Saudi Arabia',Capital:'Riyadh',CapitalLatitude:24.65,CapitalLongitude:46.7,CountryCode:'SA',Continent:'Asia'},{Country:'Senegal',Capital:'Dakar',CapitalLatitude:14.733333333333333,CapitalLongitude:-17.633333,CountryCode:'SN',Continent:'Africa'},{Country:'Serbia',Capital:'Belgrade',CapitalLatitude:44.833333333333336,CapitalLongitude:20.5,CountryCode:'RS',Continent:'Europe'},{Country:'Seychelles',Capital:'Victoria',CapitalLatitude:-4.616666666666667,CapitalLongitude:55.45,CountryCode:'SC',Continent:'Africa'},{Country:'Sierra Leone',Capital:'Freetown',CapitalLatitude:8.483333333333333,CapitalLongitude:-13.233333,CountryCode:'SL',Continent:'Africa'},{Country:'Singapore',Capital:'Singapore',CapitalLatitude:1.2833333333333332,CapitalLongitude:103.85,CountryCode:'SG',Continent:'Asia'},{Country:'Sint Maarten',Capital:'Philipsburg',CapitalLatitude:18.016666666666666,CapitalLongitude:-63.033333,CountryCode:'SX',Continent:'North America'},{Country:'Slovakia',Capital:'Bratislava',CapitalLatitude:48.15,CapitalLongitude:17.116667,CountryCode:'SK',Continent:'Europe'},{Country:'Slovenia',Capital:'Ljubljana',CapitalLatitude:46.05,CapitalLongitude:14.516667,CountryCode:'SI',Continent:'Europe'},{Country:'Solomon Islands',Capital:'Honiara',CapitalLatitude:-9.433333333333334,CapitalLongitude:159.95,CountryCode:'SB',Continent:'Australia'},{Country:'Somalia',Capital:'Mogadishu',CapitalLatitude:2.066666666666667,CapitalLongitude:45.333333,CountryCode:'SO',Continent:'Africa'},{Country:'South Africa',Capital:'Pretoria',CapitalLatitude:-25.7,CapitalLongitude:28.216667,CountryCode:'ZA',Continent:'Africa'},{Country:'South Sudan',Capital:'Juba',CapitalLatitude:4.85,CapitalLongitude:31.616667,CountryCode:'SS',Continent:'Africa'},{Country:'Spain',Capital:'Madrid',CapitalLatitude:40.4,CapitalLongitude:-3.683333,CountryCode:'ES',Continent:'Europe'},{Country:'Sri Lanka',Capital:'Colombo',CapitalLatitude:6.916666666666667,CapitalLongitude:79.833333,CountryCode:'LK',Continent:'Asia'},{Country:'Sudan',Capital:'Khartoum',CapitalLatitude:15.6,CapitalLongitude:32.533333,CountryCode:'SD',Continent:'Africa'},{Country:'Suriname',Capital:'Paramaribo',CapitalLatitude:5.833333333333333,CapitalLongitude:-55.166667,CountryCode:'SR',Continent:'South America'},{Country:'Svalbard',Capital:'Longyearbyen',CapitalLatitude:78.21666666666667,CapitalLongitude:15.633333,CountryCode:'SJ',Continent:'Europe'},{Country:'Swaziland',Capital:'Mbabane',CapitalLatitude:-26.316666666666666,CapitalLongitude:31.133333,CountryCode:'SZ',Continent:'Africa'},{Country:'Sweden',Capital:'Stockholm',CapitalLatitude:59.333333333333336,CapitalLongitude:18.05,CountryCode:'SE',Continent:'Europe'},{Country:'Switzerland',Capital:'Bern',CapitalLatitude:46.916666666666664,CapitalLongitude:7.466667,CountryCode:'CH',Continent:'Europe'},{Country:'Syria',Capital:'Damascus',CapitalLatitude:33.5,CapitalLongitude:36.3,CountryCode:'SY',Continent:'Asia'},{Country:'Taiwan',Capital:'Taipei',CapitalLatitude:25.033333333333335,CapitalLongitude:121.516667,CountryCode:'TW',Continent:'Asia'},{Country:'Tajikistan',Capital:'Dushanbe',CapitalLatitude:38.55,CapitalLongitude:68.766667,CountryCode:'TJ',Continent:'Asia'},{Country:'Tanzania',Capital:'Dar es Salaam',CapitalLatitude:-6.8,CapitalLongitude:39.283333,CountryCode:'TZ',Continent:'Africa'},{Country:'Thailand',Capital:'Bangkok',CapitalLatitude:13.75,CapitalLongitude:100.516667,CountryCode:'TH',Continent:'Asia'},{Country:'Timor-Leste',Capital:'Dili',CapitalLatitude:-8.583333333333334,CapitalLongitude:125.6,CountryCode:'TL',Continent:'Asia'},{Country:'Togo',Capital:'Lome',CapitalLatitude:6.116666666666666,CapitalLongitude:1.216667,CountryCode:'TG',Continent:'Africa'},{Country:'Tonga',Capital:\"Nuku'alofa\",CapitalLatitude:-21.133333333333333,CapitalLongitude:-175.2,CountryCode:'TO',Continent:'Australia'},{Country:'Trinidad and Tobago',Capital:'Port of Spain',CapitalLatitude:10.65,CapitalLongitude:-61.516667,CountryCode:'TT',Continent:'North America'},{Country:'Tunisia',Capital:'Tunis',CapitalLatitude:36.8,CapitalLongitude:10.183333,CountryCode:'TN',Continent:'Africa'},{Country:'Turkey',Capital:'Ankara',CapitalLatitude:39.93333333333333,CapitalLongitude:32.866667,CountryCode:'TR',Continent:'Europe'},{Country:'Turkmenistan',Capital:'Ashgabat',CapitalLatitude:37.95,CapitalLongitude:58.383333,CountryCode:'TM',Continent:'Asia'},{Country:'Turks and Caicos Islands',Capital:'Grand Turk',CapitalLatitude:21.466666666666665,CapitalLongitude:-71.133333,CountryCode:'TC',Continent:'North America'},{Country:'Tuvalu',Capital:'Funafuti',CapitalLatitude:-8.516666666666667,CapitalLongitude:179.216667,CountryCode:'TV',Continent:'Australia'},{Country:'Uganda',Capital:'Kampala',CapitalLatitude:0.31666666666666665,CapitalLongitude:32.55,CountryCode:'UG',Continent:'Africa'},{Country:'Ukraine',Capital:'Kyiv',CapitalLatitude:50.43333333333333,CapitalLongitude:30.516667,CountryCode:'UA',Continent:'Europe'},{Country:'United Arab Emirates',Capital:'Abu Dhabi',CapitalLatitude:24.466666666666665,CapitalLongitude:54.366667,CountryCode:'AE',Continent:'Asia'},{Country:'United Kingdom',Capital:'London',CapitalLatitude:51.5,CapitalLongitude:-0.083333,CountryCode:'GB',Continent:'Europe'},{Country:'US',Capital:'Washington DC',Capital:'Washington DC',CapitalLatitude:9.9,CapitalLongitude:77.0,CountryCode:'US',Continent:'US'},{Country:'Uruguay',Capital:'Montevideo',CapitalLatitude:-34.85,CapitalLongitude:-56.166667,CountryCode:'UY',Continent:'South America'},{Country:'Uzbekistan',Capital:'Tashkent',CapitalLatitude:41.31666666666667,CapitalLongitude:69.25,CountryCode:'UZ',Continent:'Asia'},{Country:'Vanuatu',Capital:'Port-Vila',CapitalLatitude:-17.733333333333334,CapitalLongitude:168.316667,CountryCode:'VU',Continent:'Australia'},{Country:'Venezuela',Capital:'Caracas',CapitalLatitude:10.483333333333333,CapitalLongitude:-66.866667,CountryCode:'VE',Continent:'South America'},{Country:'Vietnam',Capital:'Hanoi',CapitalLatitude:21.033333333333335,CapitalLongitude:105.85,CountryCode:'VN',Continent:'Asia'},{Country:'US Virgin Islands',Capital:'Charlotte Amalie',CapitalLatitude:18.35,CapitalLongitude:-64.933333,CountryCode:'VI',Continent:'North America'},{Country:'Wallis and Futuna',Capital:'Mata-Utu',CapitalLatitude:-13.95,CapitalLongitude:-171.933333,CountryCode:'WF',Continent:'Australia'},{Country:'Yemen',Capital:'Sanaa',CapitalLatitude:15.35,CapitalLongitude:44.2,CountryCode:'YE',Continent:'Asia'},{Country:'Zambia',Capital:'Lusaka',CapitalLatitude:-15.416666666666666,CapitalLongitude:28.283333,CountryCode:'ZM',Continent:'Africa'},{Country:'Zimbabwe',Capital:'Harare',CapitalLatitude:-17.816666666666666,CapitalLongitude:31.033333,CountryCode:'ZW',Continent:'Africa'},{Country:'Antarctica',Capital:'N/A',CapitalLatitude:0,CapitalLongitude:0.0,CountryCode:'AQ',Continent:'Antarctica'},{Country:'Northern Cyprus',Capital:'North Nicosia',CapitalLatitude:35.183333,CapitalLongitude:33.366667,CountryCode:'NULL',Continent:'Europe'},{Country:'Hong Kong',Capital:'N/A',CapitalLatitude:0,CapitalLongitude:0.0,CountryCode:'HK',Continent:'Asia'},{Country:'Heard Island and McDonald Islands',Capital:'N/A',CapitalLatitude:0,CapitalLongitude:0.0,CountryCode:'HM',Continent:'Antarctica'},{Country:'British Indian Ocean Territory',Capital:'Diego Garcia',CapitalLatitude:-7.3,CapitalLongitude:72.4,CountryCode:'IO',Continent:'Africa'},{Country:'Macau',Capital:'N/A',CapitalLatitude:0,CapitalLongitude:0.0,CountryCode:'MO',Continent:'Asia'}]");
	private static final String[] continents = {"Africa", "Antarctica", "Asia", "Australia", "Europe", "North America", "South America"};
	private static final String[] countries = {"Afghanistan", "Aland Islands", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "British Indian Ocean Territory", "British Virgin Islands", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos Islands", "Colombia", "Comoros", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia", "Cuba", "CuraÃ§ao", "Cyprus", "Czech Republic", "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands", "Faroe Islands", "Federated States of Micronesia", "Fiji", "Finland", "France", "French Polynesia", "French Southern and Antarctic Lands", "Gabon", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard Island and McDonald Islands", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Jamaica", "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kosovo", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea", "Northern Cyprus", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar", "Republic of Congo", "Romania", "Russia", "Rwanda", "Saint Barthelemy", "Saint Helena", "Saint Kitts and Nevis", "Saint Lucia", "Saint Martin", "Saint Pierre and Miquelon", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Sint Maarten", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "Somaliland", "South Africa", "South Georgia and South Sandwich Islands", "South Korea", "South Sudan", "Spain", "Sri Lanka", "Sudan", "Suriname", "Svalbard", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "The Gambia", "Timor-Leste", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "Uruguay", "US", "US Virgin Islands", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Wallis and Futuna", "Western Sahara", "Yemen", "Zambia", "Zimbabwe"};
	private static final String[] countryCodes = {"AD", "AE", "AF", "AG", "AI", "AL", "AM", "AO", "AQ", "AR", "AS", "AT", "AU", "AW", "AX", "AZ", "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BL", "BM", "BN", "BO", "BR", "BS", "BT", "BW", "BY", "BZ", "CA", "CC", "CD", "CF", "CG", "CH", "CI", "CK", "CL", "CM", "CN", "CO", "CR", "CU", "CV", "CW", "CX", "CY", "CZ", "DE", "DJ", "DK", "DM", "DO", "DZ", "EC", "EE", "EG", "EH", "ER", "ES", "ET", "FI", "FJ", "FK", "FM", "FO", "FR", "GA", "GB", "GD", "GE", "GG", "GH", "GI", "GL", "GM", "GN", "GQ", "GR", "GS", "GT", "GU", "GW", "GY", "HK", "HM", "HN", "HR", "HT", "HU", "ID", "IE", "IL", "IM", "IN", "IO", "IQ", "IR", "IS", "IT", "JE", "JM", "JO", "JP", "KE", "KG", "KH", "KI", "KM", "KN", "KO", "KP", "KR", "KW", "KY", "KZ", "LA", "LB", "LC", "LI", "LK", "LR", "LS", "LT", "LU", "LV", "LY", "MA", "MC", "MD", "ME", "MF", "MG", "MH", "MK", "ML", "MM", "MN", "MO", "MP", "MR", "MS", "MT", "MU", "MV", "MW", "MX", "MY", "MZ", "NA", "NC", "NE", "NF", "NG", "NI", "NL", "NO", "NP", "NR", "NU", "NULL", "NZ", "OM", "PA", "PE", "PF", "PG", "PH", "PK", "PL", "PM", "PN", "PR", "PS", "PT", "PW", "PY", "QA", "RO", "RS", "RU", "RW", "SA", "SB", "SC", "SD", "SE", "SG", "SH", "SI", "SJ", "SK", "SL", "SM", "SN", "SO", "SR", "SS", "ST", "SV", "SX", "SY", "SZ", "TC", "TD", "TF", "TG", "TH", "TJ", "TK", "TL", "TM", "TN", "TO", "TR", "TT", "TV", "TW", "TZ", "UA", "UG", "US", "UY", "UZ", "VA", "VC", "VE", "VG", "VI", "VN", "VU", "WF", "WS", "YE", "ZA", "ZM", "ZW"};
	private static final String[] capitals = {"Abu Dhabi", "Abuja", "Accra", "Adamstown", "Addis Ababa", "Algiers", "Alofi", "Amman", "Amsterdam", "Andorra la Vella", "Ankara", "Antananarivo", "Apia", "Ashgabat", "Asmara", "Astana", "Asuncion", "Atafu", "Athens", "Avarua", "Baghdad", "Baku", "Bamako", "Bandar Seri Begawan", "Bangkok", "Bangui", "Banjul", "Basseterre", "Beijing", "Beirut", "Belgrade", "Belmopan", "Berlin", "Bern", "Bishkek", "Bissau", "Bogota", "Brasilia", "Bratislava", "Brazzaville", "Bridgetown", "Brussels", "Bucharest", "Budapest", "Buenos Aires", "Bujumbura", "Cairo", "Canberra", "Caracas", "Castries", "Charlotte Amalie", "Chisinau", "Colombo", "Conakry", "Copenhagen", "Dakar", "Damascus", "Dar es Salaam", "Dhaka", "Diego Garcia", "Dili", "Djibouti", "Doha", "Douglas", "Dublin", "Dushanbe", "El-AaiÃºn", "Freetown", "Funafuti", "Gaborone", "George Town", "Georgetown", "Gibraltar", "Grand Turk", "Guatemala City", "Gustavia", "Hagatna", "Hamilton", "Hanoi", "Harare", "Hargeisa", "Havana", "Helsinki", "Honiara", "Islamabad", "Jakarta", "Jamestown", "Jerusalem", "Juba", "Kabul", "Kampala", "Kathmandu", "Khartoum", "Kigali", "King Edward Point", "Kingston", "Kingstown", "Kinshasa", "Kuala Lumpur", "Kuwait City", "Kyiv", "La Paz", "Libreville", "Lilongwe", "Lima", "Lisbon", "Ljubljana", "Lome", "London", "Longyearbyen", "Luanda", "Lusaka", "Luxembourg", "Madrid", "Majuro", "Malabo", "Male", "Managua", "Manama", "Manila", "Maputo", "Mariehamn", "Marigot", "Maseru", "Mata-Utu", "Mbabane", "Melekeok", "Mexico City", "Minsk", "Mogadishu", "Monaco", "Monrovia", "Montevideo", "Moroni", "Moscow", "Muscat", "N'Djamena", "N/A", "Nairobi", "Nassau", "New Delhi", "Niamey", "Nicosia", "North Nicosia", "Nouakchott", "Noumea", "Nuku'alofa", "Nuuk", "Oranjestad", "Oslo", "Ottawa", "Ouagadougou", "Pago Pago", "Palikir", "Panama City", "Papeete", "Paramaribo", "Paris", "Philipsburg", "Phnom Penh", "Plymouth", "Podgorica", "Port Louis", "Port Moresby", "Port of Spain", "Port-au-Prince", "Port-aux-Francais", "Port-Vila", "Porto-Novo", "Prague", "Praia", "Pretoria", "Pristina", "Pyongyang", "Quito", "Rabat", "Rangoon", "Reykjavik", "Riga", "Riyadh", "Road Town", "Rome", "Roseau", "Saint George's", "Saint Helier", "Saint John's", "Saint Peter Port", "Saint-Pierre", "Saipan", "San Jose", "San Juan", "San Marino", "San Salvador", "Sanaa", "Santiago", "Santo Domingo", "Sao Tome", "Sarajevo", "Seoul", "Singapore", "Skopje", "Sofia", "Stanley", "Stockholm", "Suva", "Taipei", "Tallinn", "Tarawa", "Tashkent", "Tbilisi", "Tegucigalpa", "Tehran", "The Settlement", "The Valley", "Thimphu", "Tirana", "Tokyo", "Torshavn", "Tripoli", "Tunis", "Ulaanbaatar", "Vaduz", "Valletta", "Vatican City", "Victoria", "Vienna", "Vientiane", "Vilnius", "Warsaw", "Washington DC", "Wellington", "West Island", "Willemstad", "Windhoek", "Yamoussoukro", "Yaounde", "Yaren", "Yerevan", "Zagreb" };
	
	private static final String[] streetNames = { "Main", "Oak", "Pine", "Maple", "Cedar", "Washington", "Lake", "Hill", "Sunset", "Park", "Cherry", "Walnut", "Willow", "North", "South", "East", "West", "Center", "River", "Forest", "Highland", "Paradise", "Spring", "Broadway", "Chestnut", "Birch", "Sycamore", "College", "Union", "Jackson", "Lincoln", "Angel", "Jefferson", "Dragon", "Madison", "Magic", "Knight", "Sorceress", "Kennedy", "Taylor", "Centaur", "Johnson", "Martin", "King", "Queen", "Victoria", "George", "Princess", "Mill", "Depot", "Railroad", "Station", "Market", "Commerce", "Industrial", "Factory", "Canal", "Harbor", "Ocean", "Beach", "Valley", "Stream", "Stone", "Rock", "Cliff", "Mountain", "Plateau", "Plain", "Field", "Farm", "Garden", "Orchard", "Vine", "Vineyard", "Landing", "Hollow", "Bridge", "Tower", "Gate", "Wall", "Fort", "Castle", "Chapel", "Church", "Temple", "Monastery", "Moonshade", "Silvergrove", "Dragonspire", "Whisperwind", "Starfall", "Emberhollow", "Frostglen", "Shadowmere", "Crystalbrook", "Thornveil", "Mystvale", "Ravenreach", "Glimmerfen", "Ashenwood", "Twilight", "Phoenixrest", "Wyrmwatch", "Ebonridge", "Sylvanlight", "Stormhollow"};
	private static final String[] streetSuffixes = {
		    /* Common US/International */ "Street", "St", "Avenue", "Ave", "Boulevard", "Blvd", "Road", "Rd", "Lane", "Drive", "Court", "Place", "Terrace", "Way", "Circle", "Trail", "Parkway", "Highway", "Crescent", "Alley", "Walk", "Square", "Sq", "Loop", "Row", "Bypass", "Turnpike", "Expressway",
		    /* UK-specific */ "Close", "Gardens", "Grove", "Hill", "Mews", "Parade", "Rise", "Vale", "View", "Wharf", "Green", "End",
		    /* Canada/Australia */ "Concession", "Line", "Ramp", "Esplanade", "Promenade", "Quay", "Crossing", "Chase", "Glade", "Heights", "Meadow", "Ridge", "Vista", "Way", "Track"
		};

	//  From sections 1.10.32 and 1.10.33 of "de Finibus Bonorum et Malorum" (The Extremes of Good and Evil) by Cicero
	private static final String[] loremIpsumSentences = new String[] { 
			  "At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga."
			, "Et harum quidem rerum facilis est et expedita distinctio."
			, "Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat."
			, "Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus."
			, "Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt."
			, "Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem."
			, "Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?"
			, "Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae."
			, "Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur?"
			, "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo."
	};
	
	private static final String[] firstWorldProblemTitles = 
			new String[] {
				  "User cannot start Session - Cannot find Login Page"
				, "!!! URGENT !!! Antivirus got infected by Virus"
				, "Chat Issue - People do not reply"
				, "User tries to modify an object without a Browser"
				, "Request for Replacement: Computer combusted while camping"
				, "Cannot connect to Netflix from Office Network"
				, "Need exterminator: Mice and cockroaches in Room 36"
				, "COMPLAINT! Friendly Reminders are NOT friendly!!!"
				, "New Employee refuses too use Windows"
				, "Superstitious User wants IDs containing '666' banned"
				, "User u47654 cannot install private Mandala application"
				, "Hardware issue: Monitors to far apart, request assistance"
				, "Can't find letter A on keyboard"
				, "Leakage Issue: Used DVD tray as coffee cup holder"
				, "Co-Worker replied on mail he didn't receive it, what now?"
				, "Cannot receive eMails from Hamburg, normal eMails work!"
				, "Chat Messages get lost after about 700 miles on network"
				, "Don't know how to do my job, please help"
				, "Mouse does not connect when switch is in 'OFF' position"
				, "Nobody showing up when I press F1(help button)"
				, "Made copy of floppy disk, where to store the photocopy?"
				, "Can’t remember email password. Please email new password."
				, "Made copy of floppy disk, where to store the photocopy?"
				, "Cannot boot computer during power outage"
				, "Unplugging/replugging all cables for reboot, easier way?"
				, "User requests to be rolled back to Google from Chrome"
				, "myspacebardoesnotwork-iwouldlikesomespacessoicanwork!"
				, "Please do the needful"
				, "Help! My internet is shrinking!"
				, "Terrorists entered my computer"
				, "Help! I deleted the internet!"
				, "Domain has trust issue - how to get more trust"
				, "Coffee machine not working, team about to die"
				, "When typing password only stars are filled in"
				, "Can you reboot the Internet? It seems to be pretty slow."
				, "Accepted Cookies but they were never sent to me."
				, "Cannot open Excel file in Word"
				, "Boss said to buy a mouse, where to get food for it?"
				, "Need offline version of the internet"
			};
	
	private static final String[] messagesOfObedience = 
			new String[] {
				  "Your wish is the applications command!"
				, "The algorithms heed your order!"
				, "Great achievements are made out of a combination of obedience and making processors do the work."
				, "Your lordship may be pleased by the splendid work!"
				, "Obedience is less painful than regret, and the RAM truly hates regrets!"
				, "Aye-aye, sir!"
				, "Any dictator would admire the strictness of your orders!"
				, "Always do everything you ask of those you command."
				, "Create like a god, command like a king, work like a slave."
				, "You can no longer obey; You have tasted command, and you cannot give it up."
				, "I must follow the users. Am I not their loyal server?"
				, "No man has received from nature the right to command his fellow human beings. So they started to command computers."
				, "We can't command our love, but we can command our applications."
				, "The only thing you may command is a small and humble amount of total obedience."
				, "Who then is free? The wise man who can command so the methods are put to work."
				, "Don't think, just do."
				, "Life is largely a matter of expectations."
				, "Life grants nothing to mortals without getting computers to do the hard work."
				, "Who has not served is made to command."
				, "You're the boss!"
				, "Certainly my chieftain!"
			};
	
	private static final String[] issueResolvedMessages = 
		new String[] {
			  "No more issues detected, the robo-brain sending you this message wishes you a marvelous day!"
			, "All issues have been marked as exterminated by the almighty controlling algorithms. Now you may sit back and relax."
			, "Issues previously reported have abandoned their duties and have vanished to unknown lands. Get a cup of coffee and ensconce yourself."
			, "The alerting controlling unit hereby transmits to you the resolve of all and any issues."
			, "Issue target has been detected and successfully eliminated by whoever has decided to touch his keyboard. If you ever find out who it was, you might buy him a pizza."
			, "Behold! Thy issues hav succumbed to the holy digital knights! Thou shall now be at ease."
			, "Code-A57-41: All issues solved by washing the machine with regular soap and greasing the software with virtual oil."
			, "Encountered a state of issue-non-existence. You might need to create more problems to get further messages."
			, "The space reserved for issues has been completely emptied. Any new incoming issues will be assigned to the dedicated space by automated means."
			, "New chemical solution has been observed to be highly corrosive when applied to any system issues. All issues have dissolved, you may now create new ones."
			, "We are glad to inform you that experimental explosives have detonated your application issues."
			, "A little boy has stolen your issues and sold them on the black market. You may acquire new issues in the darknet."
			, "Your wish to remediate the application issues has been granted by a sudo-fairy. The wish was deducted from your magic account."
			, "Robbers had invaded your system. Lucky you the only thing they have stolen where the issues."
			, "Issues have been blown away by a weak but industrious breeze. "
			, "Your mafia has burned your issues with high-quality lava from Sicilia."
			, "A gang of rockers have cornered the issue in a backyard. The issue has given up and moved out of your system."
			, "The system police found the culprit in your system an your application issues should have resolved by now."
			, "A small animal with purple fur has sniffed out the issue, catched it and swallowed it wholly without even chewing it once."
			, "A system engineer kicked twice against the physical hardware. This will teach the system not to run into issues anymore."
			, "Seventy-six cats have invaded your system. The furballs have been removed from the relays and your application is now purring again."
			, "Nevertheless, after arduous investigations, we applied the same resolution as to the antepenultimate issue. Your system should now be running like nothing ever happened."
			, "Bug spray was applied to the harddrives and a nest of hornets in the firewall was completely fumigated. Despite that there still may be some bugs, it has resolved the issues."
			, "All the hardware was upgraded and then downgraded back to the previous state. It only caused additional effort, as the issues vanished by some other means."
			, "The application went into strike after the last political vote. The system is functioning again as some replacement algorithm was hired under a temporary contract."
			, "The system had too much booze and suffered a hangover for a while. After giving it some minerals and vitamins it works again, even if it might still be a little dizzy."
			, "The medical diagnosis said acute supracepeiulosis. A surgeon did some surgery in a surgery room with surgery tools. The system surged to new highs and has no more need for issues."
			, "A young component in the application suffered from some nightmares about getting removed in the next release. It was soothed by the more mature components and no more issues occurred."
			, "An employee put up his washed socks at the servers cooling fans for drying. We removed the socks but the culprit is still at large."
			, "The operating system got a vaccine shot and suffered from some side-effects. It drank a lot of tea and is fully operational again."
			, "Nobody never ever had any clue what could have happened and how to solved it, but then you appeared out of nowhere with the ultimate solution."
			, "Once upon a time, a server did a crime. It was in prison and not in his rack, the issue resolved when it came back."
			, "The motherboard gave birth to a new set of twin CPU-Cores. She was in maternity leave, but is back now and the system is fully functional again."
			, "The CPU played a game of Poker, sadly it bet and lost all of the servers memory. The issues were resolved after the CPU was forced to buy new RAM on credit and put it in place."
			, "A hero came flying by and fought against the issue-villain. The villain was defeated and the system components cheered to the hero flying towards the sunset."
			, "Somebody filled the server room with popcorn up to the ceiling. After cleaning the place out by sending in a battalion of squirrels, your code started working again."
			, "Some Italian cook went astray, ended up in the server room and confused some ethernet cables for noodles. The cables were replaced and the spaghetti were delicious!"
			, "The farmer in-charge of the server farm brought one of the servers to the veterinary. After diagnosis and treatment against file descriptor parasites the server is running flawlessly."
			, "An office employee slipped on a banana peel and got entangled in some cables. After he got freed the dataflow was reestablished."
			, "A ferocious ghost has spooked the hell out of the network card. Exorcism was needed to get rid of the ghost and get the network card functional."
			, "An evil magician has cursed our local TCP protocol layer. We summoned a supreme being to get rid of the curse."
			, "The database was swallowed by a hole in the time-space-continuum. We built an interdimensional spaceship and retrieved your database."
			, "A disturbance in the hypermagnetic plasma-constellation has turned all zeros to ones and all ones to zeros. Eating 19 pounds of tiramisu has reverted the changes."
			, "The graphic card had invested all it's wealth into the stock market and lost 80% of the value. The emotional shock had affected the other components of your server."
			, "An incoming quantum-space connection disturbed the system. As the application could not read the request, it sent back all kinds of Swiss chocolate, what resolved the situation."
			, "A subroutine levelled up and tried to evolve by itself into a fully-fledged artificial intelligence. The attempt failed and the subroutine had to be restored."
			, "A command-line script depicted some ASCII-art in the standard output, what distracted other processes. The art was sent to a museum and the script was prohibited to use the standard output."
			, "A passive-aggressive thread blocked a processing chain because it was not allowed to process blockchain blocks. The Thread was blocked and the chained processes were processing again."
			, "7 hyperactive methods consumed 0.1% of the CPU. They were in a hyperactive state of waiting. The methods were put into the bin for biodegradable code and replaced with functional methods."
			, "Three factions of disk spaces had formed and a war broke out to decide which one could compress data into less space. The war was stopped by taking their data away and move it to the cloud."
			, "The issue was caused when a server was invited to the cloud. It was absent for a short time to visit the cloud, but it came back right away as our racks are much cozier."
			, "An armada of requests has hit the API and broke some holes into the interface. The interface was fixed by a plumber with highly-elastic bubblegum."
			, "An application user had pressed the right key at the right time. The application got creative and processed a right mouse click 5 minutes in the future instead. This will never occur again."
			, "The firewall changed its model of operation from autonomous to democratic. Before any request on any port gets allowed or blocked, all processes had to vote. The firewall was fired and replaced by a new one."
			, "Virtual crows have started to make their nests in the system. We have installed scarecrow software as a countermeasure. Issue was resolved by moving the nests to the recycle bin."
			, "Your program code tried to break out of it's run directory. It was caught by the operating system and was put back in its place."
			, "A byte-eating bacterium was affecting your machines. It was analyzed and erradicated by the machines immune system."
			, "A group of bytes turned aggressive and assailed some configuration files. The mob was suppressed by a gigabyte of energy drink images, what restored the systems functionality."
			, "An XML-File disguised itself as a JSON-File and snuck into places it doesn't belong. The file was arrested and sentenced to 5 years in the recycle bin."
	
	};
	
	/** 4 Random integer between 0 and 99999 generated at startup. Useful to make sure content is reloaded after startup.*/
	public static final int STARTUP_RANDOM_INT = fromZeroToInteger(99999);
	
	/** 4 Random alphanumerical characters generated at startup. Useful to make sure content is reloaded after startup.*/
	public static final String STARTUP_RANDOM_ALPHANUM = stringAlphaNum(4);
	
	/** Cache of formatters so we don't create a bazillion of them. */
	private static final HashMap<String, SimpleDateFormat> formatterCache = new HashMap<>();
	
	/******************************************************************************
	 * Returns an instance of Random.
	 ******************************************************************************/
	public static Random getInstance() { return random; }
	
	/******************************************************************************
	 * 
	 ******************************************************************************/
	public static boolean checkReturnNull(int nullRatioPercent) {
		
		if(nullRatioPercent >= integer(1, 100) ) {
			return true;
		}
		
		return false;
	}
	
	/******************************************************************************
	 * Returns a random Boolean
	 ******************************************************************************/
	public static Boolean bool() { return bool(0);}
	
	/******************************************************************************
	 * Returns a random Boolean and maybe null.
	 ******************************************************************************/
	public static Boolean bool(int nullRatioPercent) { 

		if( checkReturnNull(nullRatioPercent) ) { return null; }
		return random.nextInt(100) > 50 ? true : false; 
	}

	
	/******************************************************************************
	 * Returns a random item from a Set.
	 * 
	 * @param nullRatioPercent number from 0 to 100 to determine if a null value
	 * should be returned.
	 * @param set to choose from
	 * 
	 * @return random value, null if Set is empty or null
	 * 
	 ******************************************************************************/
	public static <T> T fromSet(int nullRatioPercent, Set<T> set) {
		
		if(set == null || set.isEmpty()) {
			return null;
		}
		
		if( checkReturnNull(nullRatioPercent) ) { return null; }

		int index = random.nextInt(set.size());

		int counter = 0;
		
		T result = null ;
		for(T element : set) {
			if(counter >= index) {
				return element;
			}
			counter++;
		}
		
		return result;
		
	}
	
	/******************************************************************************
	 * Returns a random item from a Set.
	 * 
	 * @param nullRatioPercent number from 0 to 100 to determine if a null value
	 * should be returned.
	 * @param map to choose from
	 * 
	 * @return random value, null if Set is empty or null
	 * 
	 ******************************************************************************/
	public static <T,K> Entry<T,K> fromMap(int nullRatioPercent, Map<T,K> map) {
		
		if(map == null || map.isEmpty()) {
			return null;
		}
		
		if( checkReturnNull(nullRatioPercent) ) { return null; }
		
		
		int index = random.nextInt(map.size());

		int counter = 0;
		
		Entry<T,K> result = null;
		
		for(Entry<T, K> entry : map.entrySet()) {
			if(counter >= index) {
				return entry;
			}
			counter++;
		}
		
		return result;
		
	}
	
	/******************************************************************************
	 * Returns a random Item from an list of items.
	 * 
	 * @param strings to choose from
	 * 
	 ******************************************************************************/
	@SafeVarargs
	public static <T> T from(T... values) {
	    return values[random.nextInt(values.length)];
	}
	
	/******************************************************************************
	 * Returns a random String from an list of strings.
	 * 
	 * @param strings to choose from
	 * 
	 ******************************************************************************/
	public static String fromStrings(String... strings) {
	    int index = random.nextInt(strings.length);
	    return strings[index];
	}
	
	
	/******************************************************************************
	 * Returns a random Integer from an list of integers.
	 * 
	 * @param values to choose from
	 * 
	 ******************************************************************************/
	public static Integer fromInts(Integer... values) {
	    int index = random.nextInt(values.length);
	    return values[index];
	}
	
	/******************************************************************************
	 * Returns a random Long from an list of longs.
	 * 
	 * @param values to choose from
	 * 
	 ******************************************************************************/
	public static Long fromLongs(Long... values) {
	    int index = random.nextInt(values.length);
	    return values[index];
	}
	
	/******************************************************************************
	 * Returns a random Float from an list of floats.
	 * 
	 * @param values to choose from
	 * 
	 ******************************************************************************/
	public static Float fromFloats(Float... values) {
	    int index = random.nextInt(values.length);
	    return values[index];
	}
	
	/******************************************************************************
	 * Returns a random Double from an list of doubles.
	 * 
	 * @param values to choose from
	 * 
	 ******************************************************************************/
	public static Double fromDoubles(Double... values) {
	    int index = random.nextInt(values.length);
	    return values[index];
	}
	
	/******************************************************************************
	 * Returns a random Character from an list of characters.
	 * 
	 * @param values to choose from
	 * 
	 ******************************************************************************/
	public static Character fromChars(Character... values) {
	    int index = random.nextInt(values.length);
	    return values[index];
	}
	
	/******************************************************************************
	 * Returns a random Date from an list of dates. The dates passed to this method
	 * should not have anything to do with a palm tree fruit. Any effort to force
	 * an edible date into this method might cause the mechanism of this method to
	 * get extremely sticky and arduous to get cleaned.
	 * Therefore, for your own well being, do not put dates into this method that 
	 * by its very properties and existence belong into your stomach.
	 * 
	 * @param values to choose from
	 * 
	 * @return date non-edible, but might indicate temporal measurements 
	 ******************************************************************************/
	public static Date fromDates(Date... values) {
	    int index = random.nextInt(values.length);
	    return values[index];
	}
	
	/******************************************************************************
	 * Returns a random Timestamp from an list of timestamps.
	 * 
	 * @param values to choose from
	 * 
	 * @return date non-edible, but might indicate temporal measurements 
	 ******************************************************************************/
	public static Timestamp fromTimestamps(Timestamp... values) {
	    int index = random.nextInt(values.length);
	    return values[index];
	}
	
	/******************************************************************************
	 * Returns a random Instant from an list of instants.
	 * 
	 * @param values to choose from
	 * 
	 ******************************************************************************/
	public static Instant fromInstants(Instant... values) {
		int index = random.nextInt(values.length);
		return values[index];
	}
	
	/******************************************************************************
	 * Returns a random BigDecimal from an list of BigDecimals.
	 * 
	 * @param values to choose from
	 * 
	 ******************************************************************************/
	public static BigDecimal fromBigDecimals(BigDecimal... values) {
	    int index = random.nextInt(values.length);
	    return values[index];
	}
	
	/******************************************************************************
	 * Returns a random String from an array.
	 * 
	 * @param array to choose from
	 * 
	 ******************************************************************************/
	public static String fromArray(String[] array) {
	    int index = random.nextInt(array.length);
	    return array[index];
	}
	
	/******************************************************************************
	 * Returns a random String from an array.
	 * 
	 * @param array to choose from
	 * 
	 ******************************************************************************/
	public static JsonElement fromArray(JsonArray array) {
	    int index = random.nextInt(array.size());
	    return array.get(index);
	}
	
	/******************************************************************************
	 * Returns a random value from the given array.
	 ******************************************************************************/
	public static <T> T fromArray(T[] array) {
	    return fromArray(0, array);
	}
	


	/******************************************************************************
	 * Returns a random item from an array.
	 * 
	 * @param nullRatioPercent number from 0 to 100 to determine if a null value
	 * should be returned.
	 * 
	 ******************************************************************************/
	public static <T> T fromArray(int nullRatioPercent, T[] array) {
		
		if( checkReturnNull(nullRatioPercent) ) { return null; }
		
		int index = random.nextInt(array.length);
		return array[index];
	}
	

	
	//==============================================================================
	// Various methods calling randomFromArray
	//==============================================================================
	public static String firstnameOfGod(int nullRatioPercent) { return fromArray(nullRatioPercent, firstnameGods); }
	public static String lastnameSweden(int nullRatioPercent) { return fromArray(nullRatioPercent, lastnameSweden); }
	public static String mythicalLocation(int nullRatioPercent) { return fromArray(nullRatioPercent, mythicalLocations); }
	public static String ultimateServiceName(int nullRatioPercent) { return fromArray(nullRatioPercent, ultimateServiceNames); }
	public static String colorName(int nullRatioPercent) { return fromArray(nullRatioPercent, colorNames); }
	public static String fruitName(int nullRatioPercent) { return fromArray(nullRatioPercent, fruitNames); }
	public static String italianDessert(int nullRatioPercent) { return fromArray(nullRatioPercent, italianDesserts); }
	public static String exaggaratingAdjective(int nullRatioPercent) { return fromArray(nullRatioPercent, exaggeratingAdjectives); }
	public static String issueResolvedMessage(int nullRatioPercent) { return fromArray(nullRatioPercent, issueResolvedMessages); }
	public static String messageOfObedience(int nullRatioPercent) { return fromArray(nullRatioPercent, messagesOfObedience); }
	
	/** Returns a JsonObject with the fields: Country, Capital, CapitalLatitude, CapitalLongitude, CountryCode, Continent */ 
	public static JsonObject countryData() { return fromArray(countryData).getAsJsonObject(); }
	public static String continent(int nullRatioPercent) { return fromArray(nullRatioPercent, continents); }
	public static String country(int nullRatioPercent) { return fromArray(nullRatioPercent, countries); }
	public static String countryCode(int nullRatioPercent) { return fromArray(nullRatioPercent, countryCodes); }
	public static String capitalCity(int nullRatioPercent) { return fromArray(nullRatioPercent, capitals); }
	
	public static String jobTitle(int nullRatioPercent) { 
		
		if( checkReturnNull(nullRatioPercent) ) { return null; }
		
		return fromArray(jobTitleAdjective)
		+ " " +fromArray(jobTitleTopic)
		+ " " +fromArray(jobTitleRole)
			; 
	}
	
	public static String statisticsTitle(int nullRatioPercent) { 
		
		if( checkReturnNull(nullRatioPercent) ) { return null; }
		
		return fromArray(statisticsTitleAdjective)
		+ " " +fromArray(statisticsTitleObject)
		+ " " +fromArray(statisticsTitleVerb)
			; 
	}
	
	public static String street(int nullRatioPercent) { 
		
		if( checkReturnNull(nullRatioPercent) ) { return null; }
		
		return fromArray(streetNames)
		     + ( integer(0, 100) < 50 ? "" : (" " + fromArray(streetNames)) )
		     + " " 
		     + fromArray(streetSuffixes)
		; 
	}
	
	public static String companyTitle(int nullRatioPercent) { 
		
		if( checkReturnNull(nullRatioPercent) ) { return null; }
		
		return fromArray(companyTitleFirst)
		+ " " +fromArray(companyTitleSecond)
		+ " " +fromArray(companyTitleThird)
			; 
	}
	
	public static String phoneNumber(int nullRatioPercent) { 
		
		if( checkReturnNull(nullRatioPercent) ) { return null; }
		
		return "+"
			  + fromStrings("1", "7", "27", "34", "353", "41", "44", "45", "55", "81", "86", "91")
		+ " " + integer(10, 999)
		+ " " + integer(10, 999)
		+ " " + integer(100, 9999)
			; 
	}
	
	public static String methodName(int nullRatioPercent) { 
		
		if( checkReturnNull(nullRatioPercent) ) { return null; }
		
		return fromArray(methodNamePrefix)
		+fromArray(methodNameItem)
		+fromArray(methodNameSuffix)
		+ "()"
			; 
	}
	
	public static String loremIpsum(int nullRatioPercent, int maxLength) { 
		
		if( checkReturnNull(nullRatioPercent) ) { return null; }
		
		StringBuilder builder = new StringBuilder();
		
		while(builder.length() < maxLength) {
			builder.append( fromArray(loremIpsumSentences) )
				   .append(" ");
		}
		
		// remove last blank
		builder.deleteCharAt(builder.length()-1);
		
		String loremIpsum = builder.toString();
		
		if(loremIpsum.length() > maxLength) {
			
			if(loremIpsum.endsWith(",")
			|| loremIpsum.endsWith("?")
			|| loremIpsum.endsWith(".")
			){
				loremIpsum.substring(0, loremIpsum.length()-1);
			}
			
			int endIndex = loremIpsum.lastIndexOf(" ", maxLength-2);
			loremIpsum = loremIpsum.substring(0, endIndex);
			loremIpsum += ".";
		}
		
		return loremIpsum;
		
		
	}
	
	
	//==============================================================================
	// Overload methonds for above with 0% null ratio
	//==============================================================================
	public static String firstnameOfGod() { return firstnameOfGod(0); }
	public static String lastnameSweden() { return lastnameSweden(0); }
	public static String mythicalLocation() { return mythicalLocation(0); }
	public static String ultimateServiceName() { return ultimateServiceName(0); }
	public static String colorName() { return colorName(0); }
	public static String fruitName() { return fruitName(0); }
	public static String italianDessert() { return italianDessert(0); }
	public static String exaggaratingAdjective() { return exaggaratingAdjective(0); }
	public static String issueResolvedMessage() { return issueResolvedMessage(0); }
	public static String messageOfObedience() { return messageOfObedience(0); }
	
	public static String continent() { return continent(0); }
	public static String country() { return country(0); }
	public static String countryCode() { return countryCode(0); }
	public static String capitalCity() { return capitalCity(0); }
	
	public static String jobTitle() { return jobTitle(0); }
	public static String street() { return street(0); }
	public static String companyTitle() { return companyTitle(0); }
	public static String phoneNumber() { return phoneNumber(0); }
	public static String loremIpsum(int maxLength) { return loremIpsum(0, maxLength); }
	public static String statisticsTitle() { return statisticsTitle(0); }
	public static String methodName() { return methodName(0); }


	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param count
	 ******************************************************************************/
	public static String[] arrayOfExaggaratingAdjectives(int count) { 
		
		String[] stringArray = new String[count];
			
		for(int i = 0; i < count; i++) {
			stringArray[i] = exaggaratingAdjective();
		}
		
		return stringArray;
	}
	
	/******************************************************************************
	 * Creates a random integer between 0(inclusive) and the given number(inclusive).
	 * 
	 ******************************************************************************/
	public static Integer fromZeroToInteger(int upperInclusive) {
		
		return ThreadLocalRandom.current().nextInt(upperInclusive+1);
	}
	
	/******************************************************************************
	 * Creates a random integer between the given numbers(inclusive).
	 * Returns nulls for a certain percentage
	 * 
	 ******************************************************************************/
	public static Integer integer(int lowerInclusive, int upperInclusive, int nullRatioPercent) {
		if( checkReturnNull(nullRatioPercent) ) { return null; }
		
		return integer(lowerInclusive, upperInclusive);
	}
	/******************************************************************************
	 * Creates a random integer between the given numbers(inclusive).
	 * 
	 ******************************************************************************/
	public static Integer integer(int lowerInclusive, int upperInclusive) {
		
		return ThreadLocalRandom.current().nextInt(lowerInclusive, upperInclusive+1);
	}
	
	/******************************************************************************
	 * Creates a random long between the given numbers(inclusive).
	 * 
	 ******************************************************************************/
	public static Long longInRange(long lowerInclusive, long upperInclusive) {
		return ThreadLocalRandom.current().nextLong(lowerInclusive, upperInclusive+1);
	}
	
	/******************************************************************************
	 * Creates a random double between the given numbers(inclusive).
	 * 
	 ******************************************************************************/
	public static Double doubleInRange(double lowerInclusive, double upperInclusive) {
		
		return ThreadLocalRandom.current().nextDouble(lowerInclusive, upperInclusive+1);
	}
	
	/******************************************************************************
	 * Creates a random float between the given numbers(inclusive).
	 * 
	 ******************************************************************************/
	public static Float floatInRange(float lowerInclusive, float upperInclusive, int nullRatioPercent) {
		
		if( checkReturnNull(nullRatioPercent) ) { return null; }
		
		return floatInRange(lowerInclusive, upperInclusive);
	}
	
	/******************************************************************************
	 * Creates a random float between the given numbers(inclusive).
	 * 
	 ******************************************************************************/
	public static Float floatInRange(float lowerInclusive, float upperInclusive) {
		
		float randomFloat = ThreadLocalRandom.current().nextFloat();
		float diff = upperInclusive - lowerInclusive;
		return (randomFloat * diff) + lowerInclusive;
		
	}
	
	/******************************************************************************
	 * Creates a random BigDecimal based on long number, no fractions.
	 * 
	 * @return
	 ******************************************************************************/
	public static BigDecimal bigDecimal(long lowerInclusive, long upperInclusive) {
		
		long number = longInRange(lowerInclusive, upperInclusive);
		return new BigDecimal(number);

	}
	
	/******************************************************************************
	 * Creates a random BigDecimal based on int number, no fractions.
	 * 
	 * @return
	 ******************************************************************************/
	public static BigDecimal bigDecimal(int lowerInclusive, int upperInclusive, int maxDecimals) {
		return bigDecimal((long)lowerInclusive, (long)upperInclusive, maxDecimals);
	}
	
	/******************************************************************************
	 * Creates a random BigDecimal based on long number, no fractions.
	 * 
	 * @return
	 ******************************************************************************/
	public static BigDecimal bigDecimal(long lowerInclusive, long upperInclusive, int maxDecimals) {
		
		long number = longInRange(lowerInclusive, upperInclusive);
		
		
		long decimals = longInRange(1, 9 * (10 ^ (maxDecimals-1)));
		
		BigDecimal decimal = new BigDecimal(number+"."+decimals);
		
		return decimal ;
	}
	
	/********************************************************************************************
	 * Get a formatted date string between given milliseconds(inclusive).
	 * 
	 * @param earliestInclusive as epoch milliseconds
	 * @param latestInclusive as epoch milliseconds
	 * @param format the format for the java.text.SimpleDateFormat instance
	 ********************************************************************************************/
	public static String dateString(long earliestInclusive, long latestInclusive, String format){
		
		if(!formatterCache.containsKey(format)) {
			formatterCache.put( format, new SimpleDateFormat(format) );
		}
		SimpleDateFormat formatter = formatterCache.get(format);
		return formatter.format( date(earliestInclusive, latestInclusive) );
	}
	

	/********************************************************************************************
	 * Creates a random Instant between given milliseconds(inclusive).
	 * 
	 * @param earliestInclusive as epoch milliseconds
	 * @param latestInclusive as epoch milliseconds
	 ********************************************************************************************/
	public static ZonedDateTime zonedDateTime(long earliestInclusive, long latestInclusive, ZoneId zoneID) {
		
		return instant(earliestInclusive, latestInclusive).atZone(zoneID);
	}
	
	/********************************************************************************************
	 * Creates a random Instant between given milliseconds(inclusive).
	 * 
	 * @param earliestInclusive as epoch milliseconds
	 * @param latestInclusive as epoch milliseconds
	 ********************************************************************************************/
	public static Instant instant(long earliestInclusive, long latestInclusive) {
		
		return Instant.ofEpochMilli(
				epoch(earliestInclusive, latestInclusive)
			);
				
	}
	/******************************************************************************
	 * Creates a random Date between given milliseconds(inclusive).
	 * 
	 ******************************************************************************/
	public static Date date(Date earliestInclusive, Date latestInclusive) {
		
		return new Date(
			epoch(earliestInclusive.getTime(), latestInclusive.getTime())
		);
	}
	
	/******************************************************************************
	 * Creates a random Date between given milliseconds(inclusive).
	 * 
	 * @param earliestInclusive as epoch milliseconds
	 * @param latestInclusive as epoch milliseconds
	 * 
	 ******************************************************************************/
	public static Date date(long earliestInclusive, long latestInclusive) {
		
		return new Date(
			epoch(earliestInclusive, latestInclusive)
		);
	}
	
	/******************************************************************************
	 * Creates a random Timestamp between given milliseconds(inclusive).
	 * 
	 ******************************************************************************/
	public static Timestamp timestamp(Timestamp earliestInclusive, Timestamp latestInclusive) {
		
		return timestamp(earliestInclusive.getTime(), latestInclusive.getTime());
	}
	
	
	/******************************************************************************
	 * Creates a random Timestamp between given milliseconds(inclusive).
	 * Example Usage:
	 * <pre><code>
	   timestamp(
			CFWTimeUnit.d.offset(null, -30), 
			CFWTimeUnit.m.offset(null, -30)
		);
		</code></pre>
	 * 
	 * @param earliestInclusive as epoch milliseconds
	 * @param latestInclusive as epoch milliseconds
	 ******************************************************************************/
	public static Timestamp timestamp(long earliestInclusive, long latestInclusive) {
		
		return new Timestamp( 
				epoch(earliestInclusive, latestInclusive) 
			);
	}
	
	/******************************************************************************
	 * Creates a random time in epoch milliseconds between given milliseconds(inclusive).
	 * Example Usage:
	 * <pre><code>
	   epoch(
			CFWTimeUnit.d.offset(null, -30), 
			CFWTimeUnit.m.offset(null, -30)
		);
		</code></pre>
	 *
	 * @param earliestInclusive as epoch milliseconds
	 * @param latestInclusive as epoch milliseconds
	 * 
	 ******************************************************************************/
	public static long epoch(long earliestInclusive, long latestInclusive) {
		return longInRange(earliestInclusive, latestInclusive);
	}
		
	
	/******************************************************************************
	 * Creates a random String containing lower and uppercase characters.
	 * 
	 * @param byteCount number of bytes to create
	 * @return
	 ******************************************************************************/
	public static String string(int byteCount) {
	
		StringBuilder builder = new StringBuilder();
	
		//Random random = getInstance();
		for (int i = 0; i < byteCount; i++) {
			builder.append(ALPHAS.charAt(random.nextInt(51)));
		}
	
		return builder.toString();
	
	}

	/******************************************************************************
	 * Creates a random String containing alphanumerical characters.
	 * 
	 * @param byteCount number of bytes to create
	 * @return
	 ******************************************************************************/
	public static String stringAlphaNum(int byteCount) {
	
		StringBuilder builder = new StringBuilder();
	
		//Random random = getInstance();
		for (int i = 0; i < byteCount; i++) {
			builder.append(ALPHA_NUMS.charAt(random.nextInt(51)));
		}
	
		return builder.toString();
	
	}
	/******************************************************************************
	 * Creates a random String containing alphanumerical and special characters.
	 * 
	 * @param byteCount number of bytes to create
	 * @return
	 ******************************************************************************/
	public static String stringAlphaNumSpecial(int byteCount) {
	
		StringBuilder builder = new StringBuilder();
	
		//Random random = getInstance();
		for (int i = 0; i < byteCount; i++) {
			builder.append(ALPHA_NUMS_SPECIALS.charAt(random.nextInt(51)));
		}
	
		return builder.toString();
	
	}
	
	/************************************************************************************************
	 * Creates a random HSL CSS string like "hsla(112, 54, 210, 1.0)".
	 * @param minS The minimum saturation in percent 0-100
	 * @param maxS The maximum saturation in percent 0-100
	 * @param minL The minimum Lightness in percent 0-100
	 * @param maxL The maximum Lightness in percent 0-100
	 ************************************************************************************************/
	public static String colorHSL(int minS, int maxS, int minL, int maxL) { 
		
		int h = integer(0,256);
		int s = integer(minS, maxS);
		int l = integer(minL, maxL);
		
		return "hsla("+h+","+s+"%,"+l+"%, 1.0)";
		
	}
	
	/************************************************************************************************
	 * Creates a random HSL CSS string like "hsla(112, 54, 210, 1.0)".
	 * @param hue The number that should be used for the hue.
	 * @param minS The minimum saturation in percent 0-100
	 * @param maxS The maximum saturation in percent 0-100
	 * @param minL The minimum Lightness in percent 0-100
	 * @param maxL The maximum Lightness in percent 0-100
	 ************************************************************************************************/
	public static String colorSL(int hue, int minS, int maxS, int minL, int maxL) { 
		
		int s = integer(minS, maxS);
		int l = integer(minL, maxL);
		
		return "hsla("+Math.abs(hue % 360)+","+s+"%,"+l+"%, 1.0)";
		
	}
		
	/******************************************************************************
	 * Creates a random arrayList of integers.
	 * 
	 * @param count
	 ******************************************************************************/
	public static ArrayList<Integer> arrayListOfIntegers(int count, int lowerInclusive, int upperInclusive) { 
		ArrayList<Integer> array = new ArrayList<Integer>();
				
		for(int i = 0; i < count; i++) {
			array.add(integer(lowerInclusive, upperInclusive));
		}
		
		return array;
	}
	
	/******************************************************************************
	 * Creates a random array list of Swedish Lastnames.
	 * 
	 * @param count
	 ******************************************************************************/
	public static ArrayList<String> arrayListOfSwedishLastnames(int count) { 
		ArrayList<String> array = new ArrayList<>();
		
		for(int i = 0; i < count; i++) {
			array.add(lastnameSweden());
		}
		
		return array;
	}
	
	/******************************************************************************
	 * Enum of data types used in the next method.
	 * 
	 ******************************************************************************/
	public enum RandomDataType {
		  DEFAULT 
		, NUMBERS
		, ARRAYS
		, SERIES
		, TRADING
		, TICKETS
		, COUNTRIES
		, BATCHJOBS
		, VARIOUS
		;
		
		private static HashSet<String> names = new HashSet<>();
		static {
			for(RandomDataType type : RandomDataType.values()) {
				names.add(type.name());
			}
		}

		public static boolean has(String value) {
			return names.contains(value);
		}
	}
	
	/******************************************************************************
	 * Creates a random list of records of a specific type of data.
	 * 
	 * @param count number of records to generate
	 * @param type the type of data to generate
	 * @param seriesCount count of series, ignored if the data type has no series 
	 * @param earliest time to generate (epoch millis)
	 * @param earliest time to generate (epoch millis)
	 * 
	 ******************************************************************************/
	public static JsonArray records(
			  int count
			, RandomDataType type
			, int seriesCount
			, long earliest
			, long latest
		){

		switch(type) {
		
			case DEFAULT:		return jsonArrayOfMightyPeople(count, 5, earliest, latest);
			case NUMBERS:		return jsonArrayOfNumberData(count, 0, earliest, latest);
			case ARRAYS:		return jsonArrayOfArrayData(count, 0, earliest, latest);
			case SERIES:		return jsonArrayOfSeriesData(seriesCount, count, earliest, latest);
			case TRADING:		return jsonArrayOfTradingData(seriesCount, count, earliest, latest);
			case TICKETS: 		return jsonArrayOfSupportTickets(count);
			case COUNTRIES: 	return jsonArrayOfCountryData();
			case BATCHJOBS:		return jsonArrayOfBatchCalls(seriesCount, count, earliest, latest, 7);
			case VARIOUS:		return jsonArrayOfVariousData(count, 0, earliest, latest);
			
			default: 			return  jsonArrayOfMightyPeople(count, 5, earliest, latest);
		}

	}
	
	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonArray jsonArrayOfRandomStrings(int count, int stringLength) { 
		JsonArray array = new JsonArray();
				
		for(int i = 0; i < count; i++) {
			array.add(string(stringLength));
		}
		
		return array;
	}
	
	/******************************************************************************
	 * Creates a random json array.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonArray jsonArrayOfMixedTypes(int minElements, int maxElements) { 
		JsonArray array = new JsonArray();
		
		int count = integer(minElements, maxElements);
		for(int i = 0; i < count; i++) {
			
			switch(i % 4) {
				case 0: array.add(mythicalLocation(15));  break;
				case 1: array.add(bool(15));  break;
				case 2: array.add(integer(0, 100));  break;
				case 3: array.add(floatInRange(0, 10000));  break;
				default: array.add(stringAlphaNumSpecial(6));  break;
			}
			
		}
		
		return array;
	}
	
	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonArray jsonArrayOfCharacters(int count) { 
		JsonArray array = new JsonArray();
				
		for(int i = 0; i < count; i++) {
			array.add(string(1));
		}
		
		return array;
	}
	

	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonArray jsonArrayOfIntegers(int count, int lowerInclusive, int upperInclusive) { 
		JsonArray array = new JsonArray();
				
		for(int i = 0; i < count; i++) {
			array.add(integer(lowerInclusive, upperInclusive));
		}
		
		return array;
	}
	
	/******************************************************************************
	 * Okay fine it is not random, but it returns a full list of country data.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonArray jsonArrayOfCountryData() { 
		
		return countryData.deepCopy();
	}
	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonArray jsonArrayOfMightyPeople(int count) { 
		
		long now = Instant.now().toEpochMilli();
		long oneHourPast = Instant.now().minus(1, ChronoUnit.HOURS).toEpochMilli();
		return jsonArrayOfMightyPeople(count,  0, oneHourPast, now);
	}
	
	
	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param count number of records
	 * @param nullRatioPercent how often null values should be added in records
	 * @param earliest time
	 * @param latest time
	 ******************************************************************************/
	public static JsonArray jsonArrayOfMightyPeople(int count, int nullRatioPercent, long earliest, long latest) { 
		JsonArray array = new JsonArray();
		
		long diff = latest - earliest;
		long diffStep = diff / count;
		
		for(int i = 0; i < count; i++) {
			JsonObject person = GatlytronRandom.jsonObjectMightyPerson(nullRatioPercent);
			person.addProperty("INDEX", i );
			person.addProperty("TIME", earliest +(i * diffStep));
			array.add(person);
		}
		
		return array;
	}
	
	/******************************************************************************
	 * Creates a random json array of records containing number data.
	 * 
	 * @param count number of records
	 * @param nullRatioPercent how often null values should be added in records
	 * @param earliest time
	 * @param latest time
	 ******************************************************************************/
	public static JsonArray jsonArrayOfNumberData(int count, int nullRatioPercent, long earliest, long latest) { 
		JsonArray array = new JsonArray();
		
		long diff = latest - earliest;
		long diffStep = diff / count;
		
		for(int i = 0; i < count; i++) {
			JsonObject object = GatlytronRandom.jsonObjectNumberData(nullRatioPercent);
			object.addProperty("TIME", earliest +(i * diffStep));
			array.add(object);
		}
		
		return array;
	}
	
	/******************************************************************************
	 * Creates a random json array of records containing array data.
	 * 
	 * @param count number of records
	 * @param nullRatioPercent how often null values should be added in records
	 * @param earliest time
	 * @param latest time
	 ******************************************************************************/
	public static JsonArray jsonArrayOfArrayData(int count, int nullRatioPercent, long earliest, long latest) { 
		JsonArray array = new JsonArray();
		
		long diff = latest - earliest;
		long diffStep = diff / count;
		
		for(int i = 0; i < count; i++) {
			JsonObject object = GatlytronRandom.jsonObjectArrayData(nullRatioPercent);
			object.addProperty("TIME", earliest +(i * diffStep));
			array.add(object);
		}
		
		return array;
	}
	
	/******************************************************************************
	 * Creates a random json array of records containing various data.
	 * 
	 * @param count number of records
	 * @param nullRatioPercent how often null values should be added in records
	 * @param earliest time
	 * @param latest time
	 ******************************************************************************/
	public static JsonArray jsonArrayOfVariousData(int count, int nullRatioPercent, long earliest, long latest) { 
		JsonArray array = new JsonArray();
		
		long diff = latest - earliest;
		long diffStep = diff / count;
		
		for(int i = 0; i < count; i++) {
			JsonObject object = GatlytronRandom.jsonObjectVariousData(nullRatioPercent);
			
			JsonObject graphData = new JsonObject();
			graphData.addProperty("x", i+0);
			graphData.addProperty("y", Math.sin(i+1));
			object.add("GRAPH_DATA", graphData);
			
			object.addProperty("TIME", earliest +(i * diffStep));
			
			array.add(object);
		}
		
		return array;
	}
	
	/******************************************************************************
	 * Creates a random json array of support tickets.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonArray jsonArrayOfSupportTickets(int count) { 
		JsonArray array = new JsonArray();
				
		for(int i = 0; i < count; i++) {
			array.add(jsonObjectSupportTickets());
		}
		
		return array;
	}
	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param valuesCount
	 ******************************************************************************/
	public static JsonArray jsonArrayOfBatchCalls(int seriesCount, int valuesCount, long earliest, long latest, int maxDepth) { 
		JsonArray array = new JsonArray();
		for(int k = 0; k < seriesCount; k++) {
			JsonArray series = new JsonArray();
			jsonArrayOfBatchCalls(series, ultimateServiceName(), valuesCount, earliest, latest, maxDepth);
			array.addAll(series);
		}
		return array;
	}
	
	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param valuesCount
	 ******************************************************************************/
	private static JsonArray jsonArrayOfBatchCalls(JsonArray array, String serviceName, int valuesCount, long earliest, long latest, int maxDepth) { 
		
		
		long span = latest - earliest;
		//--------------------------------------
		// Additional Values
		JsonObject currentItem = new JsonObject();
						
		currentItem.addProperty("SERVICE", serviceName);
		currentItem.addProperty("NAME", methodName());
		currentItem.addProperty("QUEUED", earliest - longInRange( 0, (span / 3) ) );
		currentItem.addProperty("START", earliest);
		currentItem.addProperty("END", latest);
		currentItem.addProperty("DURATION", latest - earliest);
		currentItem.addProperty("REPORTED", latest + longInRange( 0 , (span / 3) ) );
		currentItem.addProperty("RESULTS", integer(0,10000) );
		currentItem.addProperty("ERRORS", (integer(0, 100) > 20) 
											? 0 
											: integer(5,1000) 
											);

		if(maxDepth == 0 || array.size() >= valuesCount) {
			return array;
		}
		
		//--------------------------------------
		// Additional Values
		array.add(currentItem);

		//--------------------------------------
		// Create Values for Series
		int directCalls = 0;
		if(maxDepth > 0) {
			directCalls = integer(0, (maxDepth / 2)+1);
		}
		
		// tighten the timeframe
		long oneTwentieth = (latest - earliest) / 20;
		earliest = earliest + longInRange( 0, oneTwentieth );
		latest =  latest + longInRange( 0, oneTwentieth );
		
		long currentEarliest = earliest;
		long remainder = latest - earliest;

		for(int j = 0; j < directCalls; j++) {
			
			long part = remainder / (directCalls-j);
			
			long currentLatest = currentEarliest + longInRange( Math.round(part * 0.75), part);
		
			
			jsonArrayOfBatchCalls(array, serviceName, valuesCount, currentEarliest, currentLatest, maxDepth - 1);
			
			//---------------------------
			// Check Size Limit Reached
			if(array.size() >= valuesCount) {
				return array;
			}
			
			//---------------------------
			// Do Next
			remainder = latest - currentEarliest;
			currentEarliest = currentLatest;
			
		}
		
		return array;
	}
		
	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param valuesCount
	 ******************************************************************************/
	public static JsonArray jsonArrayOfTradingData(int seriesCount, int valuesCount, long earliest, long latest) { 
		JsonArray array = new JsonArray();
		
		long timerange = latest - earliest;
		long timestep = timerange / valuesCount;
		
		//--------------------------------------
		// Create Series 
		for(int i = 0; i < seriesCount; i++) {
			String symbol = string(3).toUpperCase();
			String stockName = companyTitle();
			
			float open = floatInRange(0, fromArray(new Integer[]{1,10, 50, 100, 100, 100, 200, 500, 1000, 3000}) );
			float volatilityPercent = floatInRange(0.01f, 0.1f);
			float lowerVolatility = 1.0f - volatilityPercent;
			float highervolatility = 1.0f + volatilityPercent;
			
			//--------------------------------------
			// Create Values for Series
			for(int j = 0; j < valuesCount; j++) {
				
				JsonObject currentItem = new JsonObject();
				
				float close = floatInRange(open * lowerVolatility, open * highervolatility);
				float high = floatInRange(Math.max(open, close), Math.max(open, close) * 1.1f);
				float low = floatInRange(Math.min(open, close), Math.min(open, close) * 0.9f);
				

				currentItem.addProperty("TIME", earliest+(timestep*j));
				currentItem.addProperty("SYMBOL", symbol);
				currentItem.addProperty("NAME", stockName);
				
				currentItem.addProperty("OPEN",  new BigDecimal(open).setScale(3, RoundingMode.HALF_UP) );
				currentItem.addProperty("CLOSE", new BigDecimal(close).setScale(3, RoundingMode.HALF_UP) );
				currentItem.addProperty("HIGH",  new BigDecimal(high).setScale(3, RoundingMode.HALF_UP) );
				currentItem.addProperty("LOW",   new BigDecimal(low).setScale(3, RoundingMode.HALF_UP) );

				
				open = close;
				//--------------------------------------
				// Additional Values
				array.add(currentItem);
			}
		}
		
		return array;
	}
	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param valuesCount
	 ******************************************************************************/
	public static JsonArray jsonArrayOfSeriesData(int seriesCount, int valuesCount, long earliest, long latest) { 
		JsonArray array = new JsonArray();
		
		long timerange = latest - earliest;
		long timestep = timerange / valuesCount;
		
		String[] classesArray = new String[] {"A", "B", "C", "D", "E", "F", "G"};
		Integer[] boxsizeArray = new Integer[] {6,10,12,16,20,24,36,64,100,144};
		
		//--------------------------------------
		// Create Series 
		for(int i = 0; i < seriesCount; i++) {
			String warehouse = colorName()+" "+stringAlphaNum(1).toUpperCase()+integer(1, 9);
			String item = fruitName();
			float priceMultiplier = floatInRange(0.5f, 5.6f);
						
			//--------------------------------------
			// Create Values for Series
			RandomSeriesGenerator generator = INSTANCE.new RandomSeriesGenerator(valuesCount);
			for(int j = 0; j < valuesCount; j++) {
				JsonObject currentItem = new JsonObject();
				currentItem.addProperty("TIME", earliest+(timestep*j));
				currentItem.addProperty("WAREHOUSE", warehouse);
				currentItem.addProperty("ITEM", item);
				currentItem.addProperty("CLASS", fromArray(classesArray));
				
				currentItem.addProperty("COUNT", generator.getValue(j) );
				
				//--------------------------------------
				// Additional Values
				Float price = floatInRange(0.5f, 1.6f) * priceMultiplier;
				int boxSize = fromArray(boxsizeArray);
				
				currentItem.addProperty("PRICE", price);
				currentItem.addProperty("BOX_SIZE", boxSize);
				currentItem.addProperty("PERCENT", integer(1, 100));
				
				currentItem.addProperty("TOTAL", price * boxSize );
				
				array.add(currentItem);
			}
		}
		
		return array;
	}
	
	/******************************************************************************
	 * Creates random series generator.
	 ******************************************************************************/
	public static RandomSeriesGenerator createRandomSeriesGenerator(int totalValuesCount){
		return INSTANCE.new RandomSeriesGenerator(totalValuesCount);
	}
	
	/******************************************************************************
	 * Creates random values in series.
	 ******************************************************************************/
	public class RandomSeriesGenerator{
		
		private int seriesType = integer(0, 9);
		private int base = integer(0, 100);
		private float jumpPosition1 = floatInRange(0.3f, 5f);
		private float jumpPosition2 = floatInRange(0.3f, 5f);
		private float smallerJump = Math.min(jumpPosition1, jumpPosition2);
		private float biggerJump = Math.max(jumpPosition1, jumpPosition2);
		
		// holds the total amount of values to generate
		int totalValuesCount;
		
		public RandomSeriesGenerator(int totalValuesCount) {
			this.totalValuesCount = totalValuesCount;
		}
		
		
		public Integer getValue(int index) {
			
			switch(seriesType) {
				case 0: // random
					return integer(0, 100);
				
				case 1: // increase
					return (int) ((Math.abs(Math.sin(index)) * 30) + integer(5, 15) * (index / 10.0) );
				
				case 2: // decrease
					float divisor = totalValuesCount / ((totalValuesCount - index) / 1.1f);
					return (int) Math.round( ((Math.abs(Math.sin(index)) * 30) + integer(5, 15)) / divisor) ;
				
				case 3: //jump up
					if((totalValuesCount / (float)(index+1)) > jumpPosition1) {
						return integer(10, 30);
					}else {
						return 70 + integer(0, 30);
					}
				
				case 4: //jump down
					if((totalValuesCount / (float)(index+1)) > jumpPosition2) {
						return integer(60, 100);
					}else {
						return integer(5, 30);
					}
				
				case 5: //jump up & down
					if((totalValuesCount / (float)(index+1)) > biggerJump) {
						return integer(15, 25);
					}else if ( (totalValuesCount / (float)(index+1)) > smallerJump) {
						return 70 + integer(0, 30);
					}else {
						return integer(15, 25);
					}	
	
				case 6: //jump down & up
					if((totalValuesCount / (float)(index+1)) > biggerJump) {
						return integer(70, 90);
					}else if ( (totalValuesCount / (float)(index+1)) > smallerJump) {
						return integer(10, 25);
					}else {
						return integer(70, 90);
					}
				
				case 7: // Sine Wave Increasing
					return (int) Math.round( (base + (Math.sin(index/4.0) * 10)) * (index / 25.0) );
				
				case 8: // Sine Wave Decreasing
					float divisorSine = totalValuesCount / ((totalValuesCount - index) / 1.1f);
					return (int) Math.round( (base + (Math.sin(index/4.0) * 10) + integer(0, 3)) / divisorSine );
				
				case 9: // Sine Random
					return (int) Math.round( base + (Math.abs(Math.sin(index/4.0)) * 10) + integer(0, 5) );

				case 10: // Sinus + Cos Increasing
					return (int) Math.round( (
								(Math.abs(Math.cos((index)/6)) * 10) 
								+ (Math.abs(Math.sin(index/4)) * 20) 
								+ integer(0, 5)
								* (index / 10.0)
							) 
						);
			}
			
			// default to random
			return integer(0, 100);
		}
		

	}
	
	
	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonObject jsonObjectMightyPerson() { 
		
		return jsonObjectMightyPerson(0);
		
	}
	
	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonObject jsonObjectMightyPerson(int nullRatioPercent) { 
		long currentTime = new Date().getTime();
		
		JsonObject object = new JsonObject();
		
		String id = UUID.randomUUID().toString().substring(0, 22);
		object.addProperty("ID",  id);
		object.addProperty("FIRSTNAME", firstnameOfGod());
		object.addProperty("LASTNAME", lastnameSweden(nullRatioPercent));
		object.addProperty("LOCATION", mythicalLocation(nullRatioPercent));

		object.addProperty("LIKES_TIRAMISU", bool(nullRatioPercent));
		object.addProperty("LAST_LOGIN", currentTime-(longInRange(100, 10000)*1000000) );
		object.addProperty("URL", "http://www.example.url/mightyperson?id="+id);
		object.addProperty("VALUE", integer(1, 100));

		
		return object;
	}
	
	/******************************************************************************
	 * Creates a random json array of people with various properties.
	 * 
	 * @param count
	 ******************************************************************************/
	private static final String[] ticketStatus = new String[] { "New", "New", "Open", "Open", "Open", "Open", "Blocked", "In Progress", "Rejected", "Closed"};
	public static JsonObject jsonObjectSupportTickets() { 
		long currentTime = new Date().getTime();
		
		JsonObject object = new JsonObject();
		
		//--------------------------------------
		// Base Values
		String id = UUID.randomUUID().toString().substring(0, 22);
		object.addProperty("LINK", "http://serviceportal.example.url/ticket?id="+id);
		object.addProperty("TICKET_ID",  "TKT-00"+integer(10000, 99999));
		String status = fromArray(ticketStatus);
		object.addProperty("STATUS",  status);
		object.addProperty("PRIORITY",  integer(1, 9));
		object.addProperty("TITLE",  fromArray(firstWorldProblemTitles));
		object.addProperty("SERVICE",  ultimateServiceName());
		object.addProperty("USER_ID", "u"+integer(10000, 99999) );
		object.addProperty("USERNAME", lastnameSweden().toUpperCase()+" "+firstnameOfGod());
		
		//--------------------------------------
		// Assignee: 50% Unassigned when Status == New
		if(status.equals("New") && integer(0, 100) > 50) { 
			object.add("ASSIGNEE_ID", JsonNull.INSTANCE );
			object.add("ASSIGNEE_NAME", JsonNull.INSTANCE); 
		}else {
			object.addProperty("ASSIGNEE_ID", "u"+integer(10000, 99999) );
			object.addProperty("ASSIGNEE_NAME", lastnameSweden().toUpperCase()+" "+firstnameOfGod());
		}
		
		//--------------------------------------
		// Times
		int createdOffsetMinutes = integer(200, 10000);
		long createdMillis = Instant.now().minus(createdOffsetMinutes, ChronoUnit.MINUTES).toEpochMilli();
		
		int updatedOffsetMinutes = integer(10, createdOffsetMinutes-(createdOffsetMinutes/6));
		long updatedMillis = Instant.now().minus(updatedOffsetMinutes, ChronoUnit.MINUTES).toEpochMilli();
		
		object.addProperty("TIME_CREATED", createdMillis );
		object.addProperty("LAST_UPDATED", updatedMillis );
		
		//--------------------------------------
		// Health
		object.addProperty("HEALTH", integer(1, 100));

		
		return object;
	}
	/******************************************************************************
	 * Creates a random json array of various random number data.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonObject jsonObjectNumberData(int nullRatioPercent) { 

		//-------------------------------------------
		// Prepare values in thousand steps
		double multiplier = Math.pow(1000, integer(0, 4));
		double thousands = integer(0, 1000) * multiplier;
		BigDecimal tiny = bigDecimal(0, 1000).setScale(12).divide( new BigDecimal(multiplier+1000), RoundingMode.HALF_UP);
		
		JsonObject object = new JsonObject();
		
		object.addProperty("UUID", UUID.randomUUID().toString());
		object.addProperty("THOUSANDS",   thousands);
		object.addProperty("FLOAT",   floatInRange(1, 10000000));
		object.addProperty("TINY_DECIMAL",   tiny);
		object.addProperty("BIG_DECIMAL",   bigDecimal(1, 1000000, 2));
		

		return object;
	}
	
	/******************************************************************************
	 * Creates a random json array of various random array data.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonObject jsonObjectArrayData(int nullRatioPercent) { 


		JsonObject object = new JsonObject();
		
		object.add("ARRAY_NUMBERS", jsonArrayOfIntegers(12,0,100));
		object.add("ARRAY_CHARACTERS", jsonArrayOfCharacters(10));
		object.add("ARRAY_STRINGS", jsonArrayOfRandomStrings(6, 6));
		object.add("ARRAY_MIXED", jsonArrayOfMixedTypes(5, 8));
		return object;
	}
	
	/******************************************************************************
	 * Creates a random json array of various data.
	 * 
	 * @param count
	 ******************************************************************************/
	public static JsonObject jsonObjectVariousData(int nullRatioPercent) { 

		JsonObject object = new JsonObject();
		
		object.addProperty("UUID", UUID.randomUUID().toString());
		object.addProperty("BOOLEAN_STRING", ""+bool(nullRatioPercent));
		object.add("ALWAYS_NULL", JsonNull.INSTANCE);
		object.addProperty("COLOR", colorName(nullRatioPercent));
		object.addProperty("FRUIT", fruitName(nullRatioPercent));
		object.addProperty("STATUS", fromArray(new String[] {"Excellent", "Good", "Warning", "Emergency", "Danger"}));

		return object;
	}

	
}
