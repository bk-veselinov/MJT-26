public class UniqueSubstringFinder {
    public static String longestUniqueSubstring(String s){

        int n = s.length();

        if(n==0) return "";
        if(n==1) return s;

        int l=0, r=1, maxL=0, maxR=0;
        boolean[] letters = new boolean['z'-'a'+1];

        letters[s.charAt(l)-'a'] = true;

        while(l<n && r<n){

            if (!letters[s.charAt(r)-'a']){
                letters[s.charAt(r)-'a'] = true;
                r++;

                if (r-l > maxR-maxL){
                    maxR = r;
                    maxL = l;
                }

            }else {
                letters[s.charAt(l)-'a']=false;
                l++;

            }
        }


        return s.substring(maxL, maxR);
    }

}
