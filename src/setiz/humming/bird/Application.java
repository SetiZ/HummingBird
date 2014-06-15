package setiz.humming.bird;

import com.parse.Parse;

public class Application extends android.app.Application {

	public Application() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "DN7OMoYspWuMif4xkn8ZcfJLZAHRGjapI7HrrwyP",
				"EDbmx4cgHuxWAbLMNpParqllCvVscZtSFqQuhV6o");
		//ParseTwitterUtils.initialize("ltsNHwlTmS1EElmKKVl9fr1Iy", "0gQytJG4ncT1YmRbLZtEQWTKVkI8POdInyxBUmAUz5G1RSO4Zw");
	}

}
