package life.majiang.community.service;

import life.majiang.community.enums.AdPosEnum;
import life.majiang.community.mapper.AdMapper;
import life.majiang.community.model.Ad;
import life.majiang.community.model.AdExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdService {
    @Autowired
    private AdMapper adMapper;

    public List<Ad> list(AdPosEnum posEnum){
        AdExample adExample = new AdExample();
        adExample.createCriteria()
                .andStatusEqualTo(1)
                .andGmtEndGreaterThan(System.currentTimeMillis())
                .andGmtStartLessThanOrEqualTo(System.currentTimeMillis())
                .andPosEqualTo(posEnum.name());
        List<Ad> adList = adMapper.selectByExample(adExample);
        return adList;
    }

    public Map<String, List<Ad>> posMap(){
        Map posMap = new HashMap<>();
        for (AdPosEnum adPosEnum : AdPosEnum.values()) {
            posMap.put(adPosEnum.name(), list(adPosEnum));
        }
        return posMap;
    }
}
