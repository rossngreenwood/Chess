public class MediaLoader 
{
	public static java.awt.Image loadImage(String name)
	{
		try
		{
			java.awt.Image result =  java.awt.Toolkit.getDefaultToolkit().getImage(
					MediaLoader.class.getResource(name));
			
			return result;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static java.applet.AudioClip loadSound(String name)
	{
		try
		{
			return java.applet.Applet.newAudioClip(
					MediaLoader.class.getResource(name));
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
