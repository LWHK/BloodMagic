package WayofTime.bloodmagic.api.alchemyCrafting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class AlchemyCircleRenderer {

	public float offsetFromFace = -0.9f;
	public final ResourceLocation arrayResource;

	public AlchemyCircleRenderer(ResourceLocation arrayResource) {
		this.arrayResource = arrayResource;
	}

	public float getRotation(float craftTime) {
		float offset = 2;
		if (craftTime >= offset) {
			float modifier = (float) Math.pow(craftTime - offset, 1.5);
			return modifier * 1f;
		}
		return 0;
	}

	public float getSecondaryRotation(float craftTime) {
		float offset = 50;
		if (craftTime >= offset) {
			float modifier = (float) Math.pow(craftTime - offset, 1.7);
			return modifier * 0.5f;
		}
		return 0;
	}

	public float getSizeModifier(float craftTime) {
		if (craftTime >= 150) {
			return (200 - craftTime) / 50f;
		}
		return 1.0f;
	}

	public float getVerticalOffset(float craftTime) {
		if (craftTime >= 5) {
			if (craftTime <= 40) {
				return (float) ((-0.4) * Math.pow((craftTime - 5) / 35f, 3));
			} else {
				return -0.4f;
			}
		}
		return 0;
	}

	public void renderAt(TileEntity tile, double x, double y, double z, float craftTime) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer wr = tessellator.getWorldRenderer();

		GL11.glPushMatrix();
		// float rot = (float)(this.worldObj.provider.getWorldTime() % (360 /
		// this.rotationspeed) * this.rotationspeed) + this.rotationspeed * f;
		float rot = getRotation(craftTime);
		float secondaryRot = getSecondaryRotation(craftTime);

		float size = 1.0F * getSizeModifier(craftTime);

		// Bind the texture to the circle
		Minecraft.getMinecraft().renderEngine.bindTexture(arrayResource);

		GL11.glTexParameterf(3553, 10242, 10497.0F);
		GL11.glTexParameterf(3553, 10243, 10497.0F);

		GL11.glDisable(2884);

		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 1);

		GL11.glTranslated(x, y, z);

		EnumFacing sideHit = EnumFacing.UP; // Specify which face this "circle"
											// is located on
		GL11.glTranslatef(sideHit.getFrontOffsetX() * offsetFromFace, sideHit.getFrontOffsetY()
				* offsetFromFace, sideHit.getFrontOffsetZ() * offsetFromFace);

		switch (sideHit) {
		case DOWN:
			GL11.glTranslatef(0, 0, 1);
			GL11.glRotatef(-90.0f, 1, 0, 0);
			break;
		case EAST:
			GL11.glRotatef(-90.0f, 0, 1, 0);
			GL11.glTranslatef(0, 0, -1);
			break;
		case NORTH:
			break;
		case SOUTH:
			GL11.glRotatef(180.0f, 0, 1, 0);
			GL11.glTranslatef(-1, 0, -1);
			break;
		case UP:
			GL11.glTranslatef(0, 1, 0);
			GL11.glRotatef(90.0f, 1, 0, 0);
			break;
		case WEST:
			GL11.glTranslatef(0, 0, 1);
			GL11.glRotatef(90.0f, 0, 1, 0);
			break;
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(0.5f, 0.5f, getVerticalOffset(craftTime));
		GL11.glRotatef(rot, 0, 0, 1);
		GL11.glRotatef(secondaryRot, 1, 0, 0);
		GL11.glRotatef(secondaryRot * 0.45812f, 0, 0, 1);
		double var31 = 0.0D;
		double var33 = 1.0D;
		double var35 = 0;
		double var37 = 1;

		// GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		wr.begin(7, DefaultVertexFormats.POSITION_TEX);
		// wr.setBrightness(200);
		// wr.putColorRGB_F(1, 1, 1, 1);
		// wr.putColorMultiplier(1, 1, 1, 1);
		wr.pos(size / 2f, size / 2f, 0.0D).tex(var33, var37).endVertex();
		wr.pos(size / 2f, -size / 2f, 0.0D).tex(var33, var35).endVertex();
		wr.pos(-size / 2f, -size / 2f, 0.0D).tex(var31, var35).endVertex();
		wr.pos(-size / 2f, size / 2f, 0.0D).tex(var31, var37).endVertex();
		// wr.putColorMultiplier(0.5f, 1, 1, 7);
		tessellator.draw();

		GL11.glPopMatrix();

		GL11.glDepthMask(true);
		GL11.glDisable(3042);
		GL11.glEnable(2884);
		// GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glPopMatrix();
	}
}