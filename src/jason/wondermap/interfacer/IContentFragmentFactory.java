package jason.wondermap.interfacer;

import jason.wondermap.fragment.ContentFragment;

/**
 * 内容fragment工厂接口
 * 
 * @author caiminghui
 * 
 */
public interface IContentFragmentFactory {
	/**
	 * 根据类型产生fragment
	 * 
	 * @param type
	 *            fragment类型
	 * @return fragment
	 */
	public ContentFragment createFragment(int type);

	/**
	 * fragment类型对应的名称字符串
	 * 
	 * @param type
	 *            fragment类型
	 * @return 类型对应的名称
	 */
	public String toString(int type);
}
