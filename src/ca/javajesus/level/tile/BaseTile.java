package ca.javajesus.level.tile;

import ca.javajesus.game.gfx.Screen;
import ca.javajesus.game.gfx.SpriteSheet;
import ca.javajesus.level.Level;

public class BaseTile extends Tile {
    protected int tileId;
    protected int tileColour;

    public BaseTile(int id, int x, int y, int tileColour, int levelColour) {
        super(id, false, false, levelColour, SpriteSheet.tiles);
        this.tileId = x + y * 32;
        //Trippy Mode
        //this.tileId = 20 * 32;
        this.tileColour = tileColour;
    }

    public void tick() {

    }

    public void render(Screen screen, Level level, int x, int y) {
        screen.render(x, y, tileId, tileColour, 0x00, 1, this.sheet);
    }
    
    public void setTileID(int x, int y) {
        this.tileId = x + y * 32;
    }

}
