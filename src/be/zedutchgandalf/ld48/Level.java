package be.zedutchgandalf.ld48;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Level {
	static Level current;
	int id, text, enemies;
	boolean showFrustum = false;
	static Level[] levels;
	double[] data;
	public boolean complete = false;
	static int totGoats;
	static int goats;

	public Level(int id) {
		this.id = id;
		data = new double[0];
	}

	public void load() {
		if (goats != totGoats && id == 0)
			goats = totGoats;
		enemies = 0;
		System.out.println("Loading level " + id);
		Main.instance.player.canSee = false;
		text = Main.write("Level: " + id, Main.inverse(Main.backgroundColor), 270 * Main.SCALE, 2 + 6 * Main.SCALE);
		new Entity(Main.instance, 0, 0, Main.WIDTH, 10, -1, Main.backgroundColor);
		Main.instance.player.x = data[0];
		Main.instance.player.y = data[1];
		for (int i = 2; i < data.length; i += 7) {
			if (data[i] == 0) {
				enemies++;
				new Enemy(Main.instance, data[i + 1], data[i + 2], (int) data[i + 3], (int) data[i + 4], (int) data[i + 5], data[i + 6] / 180 * Math.PI);
			} else if (data[i] == 1) {
				enemies++;
				new Blind(Main.instance, data[i + 1], data[i + 2], (int) data[i + 3], (int) data[i + 4]);
			} else if (data[i] == 2) {
				if (data[i + 3] > 1 && data[i + 4] > 1)
					for (int j = 0; j < data[i + 3]; j++)
						for (int k = 0; k < data[i + 4]; k++)
							new Block(Main.instance, data[i + 1] + j * 5, data[i + 2] + k * 5);
				else if (data[i + 3] == 1)
					for (int k = 0; k < data[i + 4]; k++)
						new Block(Main.instance, data[i + 1], data[i + 2] + k * 5);
				else if (data[i + 4] == 1)
					for (int j = 0; j < data[i + 3]; j++)
						new Block(Main.instance, data[i + 1] + j * 5, data[i + 2]);
			} else if (data[i] == 3) {
				if (data[i + 3] == 1 && data[i + 4] == 1)
					new Wall(Main.instance, data[i + 1], data[i + 2]);
				else if (data[i + 3] == 1)
					for (int j = 0; j < data[i + 4]; j++)
						new Wall(Main.instance, data[i + 1], data[i + 2] + j * 5);
				else if (data[i + 4] == 1)
					for (int j = 0; j < data[i + 3]; j++)
						new Wall(Main.instance, data[i + 1] + j * 5, data[i + 2]);
				else
					for (int j = 0; j < data[i + 3]; j++)
						for (int k = 0; k < data[i + 4]; k++)
							new Wall(Main.instance, data[i + 1] + j * 5, data[i + 2] + k * 5);
			} else if (data[i] == 4) {
				new Vault(Main.instance, data[i + 1], data[i + 2]);
			} else if (data[i] == 5) {
				new Goat(Main.instance, data[i + 1], data[i + 2]);
			}
		}
		current = this;
		complete = false;
		Main.instance.player.lives = 1;
		Main.instance.player.render = true;
		Main.instance.player.rotation = Math.PI / 2;
		Main.instance.player.shoot = 50;
		Main.instance.player.canSee = true;
	}

	public void addData(double d) {
		double[] t = new double[data.length + 1];
		for (int i = 0; i < data.length; i++) {
			t[i] = data[i];
		}
		t[data.length] = d;
		data = t;
	}

	public static void init(InputStream data) throws SAXException, IOException, ParserConfigurationException, URISyntaxException {
		DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbFact.newDocumentBuilder();
		if (data == null)
			Main.instance.stop(1);
		Document doc = db.parse(data);
		Element root = doc.getDocumentElement();
		root.normalize();
		NodeList children = root.getElementsByTagName("level");
		levels = new Level[children.getLength()];
		for (int i = 0; i < children.getLength(); i++) {
			Element lvl = ((Element) children.item(i));
			NodeList ens = lvl.getElementsByTagName("enemy");
			levels[i] = new Level(Integer.parseInt(children.item(i).getAttributes().item(0).getNodeValue()));
			if (lvl.getElementsByTagName("frustum").getLength() == 1)
				levels[i].showFrustum = true;
			levels[i].addData(Integer.parseInt(getTagValue("x", lvl.getElementsByTagName("player").item(0))));
			levels[i].addData(Integer.parseInt(getTagValue("y", lvl.getElementsByTagName("player").item(0))));
			for (int j = 0; j < ens.getLength(); j++) {
				levels[i].addData(0);	//enemy
				levels[i].addData(Integer.parseInt(getTagValue("x", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("y", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("width", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("height", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("life", ens.item(j))));
				levels[i].addData(Double.parseDouble((getTagValue("rotation", ens.item(j)))));
			}
			ens = lvl.getElementsByTagName("blind");
			for (int j = 0; j < ens.getLength(); j++) {
				levels[i].addData(1);	//blind
				levels[i].addData(Integer.parseInt(getTagValue("x", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("y", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("width", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("height", ens.item(j))));
				levels[i].addData(-1);
				levels[i].addData(-1);
			}
			ens = lvl.getElementsByTagName("wallB");
			for (int j = 0; j < ens.getLength(); j++) {
				levels[i].addData(2);	//breakable walls
				levels[i].addData(Integer.parseInt(getTagValue("x", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("y", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("width", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("height", ens.item(j))));
				levels[i].addData(-1);
				levels[i].addData(-1);
			}
			ens = lvl.getElementsByTagName("wallU");
			for (int j = 0; j < ens.getLength(); j++) {
				levels[i].addData(3);	//unbreakable walls
				levels[i].addData(Integer.parseInt(getTagValue("x", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("y", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("width", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("height", ens.item(j))));
				levels[i].addData(-1);
				levels[i].addData(-1);
			}
			ens = lvl.getElementsByTagName("vault");
			if (ens.getLength() > 0)
				if (Integer.parseInt(getTagValue("y", ens.item(0))) >= 0) {
					levels[i].enemies++;
					levels[i].addData(4);	//vault
					levels[i].addData(Integer.parseInt(getTagValue("x", ens.item(0))));
					levels[i].addData(Integer.parseInt(getTagValue("y", ens.item(0))));
					levels[i].addData(-1);
					levels[i].addData(-1);
					levels[i].addData(-1);
					levels[i].addData(-1);
				}
			ens = lvl.getElementsByTagName("goat");
			for (int j = 0; j < ens.getLength(); j++) {
				goats++;
				levels[i].addData(5);	//goat
				levels[i].addData(Integer.parseInt(getTagValue("x", ens.item(j))));
				levels[i].addData(Integer.parseInt(getTagValue("y", ens.item(j))));
				levels[i].addData(-1);
				levels[i].addData(-1);
				levels[i].addData(-1);
				levels[i].addData(-1);
			}
			totGoats = goats;
		}
		levels[0].load();
	}

	private static String getTagValue(String tag, Node node) {
		NodeList nlList = ((Element) node).getElementsByTagName(tag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}

	public void unload() {
		Main.instance.player.cleanLevel();
		Main.removeText(text);
		current = null;
	}
}
