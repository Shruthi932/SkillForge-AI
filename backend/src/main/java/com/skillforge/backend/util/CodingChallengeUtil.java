package com.skillforge.backend.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for coding challenges.
 */
public class CodingChallengeUtil {

    /**
     * Finds two indices in the array that add up to the target.
     * Returns the indices as int[2], or empty array if no such pair exists.
     * Assumes exactly one valid solution exists and indices are unique.
     *
     * @param nums the input array of integers
     * @param target the target sum
     * @return array of two indices or empty array
     */
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }
            map.put(nums[i], i);
        }
        return new int[] {};
    }
}
