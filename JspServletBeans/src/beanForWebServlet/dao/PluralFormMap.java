package beanForWebServlet.dao;

import java.util.HashMap;
import java.util.Map;

public class PluralFormMap {

	static Map<String,String> pluralFormMap;
	static PluralFormMap pluralFormMapInstance=new PluralFormMap();

	private PluralFormMap (){
		pluralFormMap=new HashMap<String,String>();

		pluralFormMap.put("addendum","addenda");
		pluralFormMap.put("alga","algae");
		pluralFormMap.put("alto","altos");
		pluralFormMap.put("alumna","alumnae");
		pluralFormMap.put("alumnus","alumni");
		pluralFormMap.put("analysis","analyses");
		pluralFormMap.put("antelope","antelopes");
		pluralFormMap.put("antenna","antennas");
		pluralFormMap.put("apex","apexes");
		pluralFormMap.put("appendix","appendixes");
		pluralFormMap.put("aquarium","aquariums");
		pluralFormMap.put("archipelago","archipelagos");

		pluralFormMap.put("automaton","automatons");
		pluralFormMap.put("axis","axes");
		pluralFormMap.put("bacillus","bacilli");
		pluralFormMap.put("bacterium","bacteria");
		pluralFormMap.put("banjo","banjoes");
		pluralFormMap.put("basis","bases");

		pluralFormMap.put("buffalo","buffalos");
		pluralFormMap.put("bureau","bureaus");
		pluralFormMap.put("cactus","cacti");
		pluralFormMap.put("calf","calves");
		pluralFormMap.put("cargo","cargoes");
		pluralFormMap.put("cello","cellos");
		pluralFormMap.put("chamois","chamois");
		pluralFormMap.put("chassis","chassis");
		pluralFormMap.put("cherub","cherubs");
		pluralFormMap.put("child","children");
		pluralFormMap.put("codex","codices");
		pluralFormMap.put("commando","commandos");
		pluralFormMap.put("concerto","concertos");
		pluralFormMap.put("contralto","contraltos");
		pluralFormMap.put("corpus","corpora");
		pluralFormMap.put("court-martial","courts-martial");
		pluralFormMap.put("crisis","crises");
		pluralFormMap.put("criterion","criteria");
		pluralFormMap.put("datum","data");
		pluralFormMap.put("deer","deer");
		pluralFormMap.put("diagnosis","diagnoses");
		pluralFormMap.put("dwarf","dwarfs");
		pluralFormMap.put("dynamo","dynamos");
		pluralFormMap.put("elf","elves");
		pluralFormMap.put("embryo","embryos");
		pluralFormMap.put("epoch","epochs");

		pluralFormMap.put("fish","fish");
		pluralFormMap.put("flounder","flounder");
		pluralFormMap.put("focus","focuses");
		pluralFormMap.put("foot","feet");
		pluralFormMap.put("formula","formulas");
		pluralFormMap.put("fungus","fungi");
		pluralFormMap.put("ganglion","ganglia");
		pluralFormMap.put("genesis","geneses");
		pluralFormMap.put("genus","genera");
		pluralFormMap.put("goose","geese");
		pluralFormMap.put("half","halves");
		pluralFormMap.put("halo","halos");
		pluralFormMap.put("herring","herrings");
		pluralFormMap.put("hippopotamus","hippopotamuses");
		pluralFormMap.put("hoof","hoofs");
		pluralFormMap.put("hypothesis","hypotheses");
		pluralFormMap.put("index","indexes");
		pluralFormMap.put("isthmus","isthmuses");
		pluralFormMap.put("Japanese","Japanese");
		pluralFormMap.put("kibbutz","kibbutzim");
		pluralFormMap.put("kilo","kilos");
		pluralFormMap.put("knife","knives");
		pluralFormMap.put("lady-in-waiting","ladies-in-waiting");
		pluralFormMap.put("larva","larvae");
		pluralFormMap.put("leaf","leaves");
		pluralFormMap.put("libretto","librettos");
		pluralFormMap.put("life","lives");
		pluralFormMap.put("loaf","loaves");
		pluralFormMap.put("locus","loci");
		pluralFormMap.put("louse","lice");
		pluralFormMap.put("magus","magi");
		pluralFormMap.put("man","men");
		pluralFormMap.put("manservant","menservants");
		pluralFormMap.put("matrix","matrices");
		pluralFormMap.put("medium","mediums");
		pluralFormMap.put("memorandum","memorandums");
		pluralFormMap.put("monarch","monarchs");
		pluralFormMap.put("money","monies");
		pluralFormMap.put("moose","moose");
		pluralFormMap.put("moratorium","moratoria");
		pluralFormMap.put("mosquito","mosquitoes");
		pluralFormMap.put("motto","mottoes");
		pluralFormMap.put("mouse","mice");
		pluralFormMap.put("nebula","nebulae");
		pluralFormMap.put("nemesis","nemeses");
		pluralFormMap.put("nucleus","nuclei");
		pluralFormMap.put("oasis","oases");
		pluralFormMap.put("octopus","octopuses");
		pluralFormMap.put("offspring","offspring");
		pluralFormMap.put("ovum","ova");
		pluralFormMap.put("ox","oxen");
		pluralFormMap.put("parenthesis","parentheses");
		pluralFormMap.put("passerby","passersby");
		pluralFormMap.put("patois","patois");
		pluralFormMap.put("phenomenon","phenomena");
		pluralFormMap.put("photo","photos");
		pluralFormMap.put("piano","pianos");
		pluralFormMap.put("piccolo","piccolos");
		pluralFormMap.put("plateau","plateaus");
		pluralFormMap.put("portmanteau","portmanteaus");
		pluralFormMap.put("potato","potatoes");
		pluralFormMap.put("quarto","quartos");
		pluralFormMap.put("quiz","quizzes");
		pluralFormMap.put("radius","radii");
		pluralFormMap.put("reindeer","reindeer");
		pluralFormMap.put("scarf","scarves");
		pluralFormMap.put("self","selves");
		pluralFormMap.put("seraph","seraphs");
		pluralFormMap.put("series","series");
		pluralFormMap.put("sheep","sheep");
		pluralFormMap.put("shelf","shelves");
		pluralFormMap.put("silo","silos");
		pluralFormMap.put("solo","solos");
		pluralFormMap.put("soprano","sopranos");
		pluralFormMap.put("stand-by","stands-by");
		pluralFormMap.put("stimulus","stimuli");
		pluralFormMap.put("stratum","strata");
		pluralFormMap.put("stylus","styli");
		pluralFormMap.put("syllabus","syllabuses");
		pluralFormMap.put("symposium","symposiums");
		pluralFormMap.put("tableau","tableaux");
		pluralFormMap.put("tango","tangos");
		pluralFormMap.put("tempo","tempos");
		pluralFormMap.put("terminus","termini");
		pluralFormMap.put("thesis","theses");
		pluralFormMap.put("thief","thieves");
		pluralFormMap.put("tobacco","tobaccos");
		pluralFormMap.put("tomato","tomatoes");
		pluralFormMap.put("tooth","teeth");
		pluralFormMap.put("tornado","tornadoes");
		pluralFormMap.put("trousseau","trousseaux");
		pluralFormMap.put("trout","trout");
		pluralFormMap.put("ultimatum","ultimatums");
		pluralFormMap.put("vertebra","vertebrae");
		pluralFormMap.put("virtuoso","virtuosos");
		pluralFormMap.put("volcano","volcanoes");
		pluralFormMap.put("vortex","vortexes");
		pluralFormMap.put("wharf","wharves");
		pluralFormMap.put("wife","wives");
		pluralFormMap.put("wolf","wolves");
		pluralFormMap.put("woman","women");
		pluralFormMap.put("zero","zeros");
	}

	public static PluralFormMap getInstance(){
		return pluralFormMapInstance;
	}

	public String getPlural(String word){
		return pluralFormMap.get(word);
	}
}
